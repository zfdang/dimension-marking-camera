package com.zfdang.dimensioncam.ui.annotation;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.zfdang.dimensioncam.data.Annotation;
import com.zfdang.dimensioncam.data.Photo;
import com.zfdang.dimensioncam.data.PhotoDao;
import com.zfdang.dimensioncam.data.Repository;
import com.zfdang.dimensioncam.data.AppDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class AnnotationViewModel extends AndroidViewModel {

    private Repository mRepository;
    private PhotoDao mPhotoDao;
    private MutableLiveData<Photo> mCurrentPhoto = new MutableLiveData<>();
    private MutableLiveData<Long> mPhotoId = new MutableLiveData<>();
    private LiveData<List<Annotation>> mAnnotations;
    
    // Undo Stack: Stores snapshots of the annotation list
    private Stack<List<Annotation>> undoStack = new Stack<>();

    public AnnotationViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
        mPhotoDao = AppDatabase.getDatabase(application).photoDao();
        
        // Initialize mAnnotations to observe mPhotoId changes
        mAnnotations = Transformations.switchMap(mPhotoId, id -> mRepository.getAnnotationsForPhoto(id));
    }

    public void loadPhoto(long photoId) {
        mPhotoId.setValue(photoId);
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Photo photo = mPhotoDao.getPhotoById(photoId);
            mCurrentPhoto.postValue(photo);
        });
    }

    public LiveData<Photo> getCurrentPhoto() {
        return mCurrentPhoto;
    }

    public LiveData<List<Annotation>> getAnnotations() {
        return mAnnotations;
    }

    public void addAnnotation(Annotation annotation) {
        saveStateForUndo();
        mRepository.insertAnnotation(annotation);
    }

    public void updateAnnotation(Annotation annotation) {
        saveStateForUndo();
        mRepository.updateAnnotation(annotation);
    }

    public void deleteAnnotation(Annotation annotation) {
        saveStateForUndo();
        mRepository.deleteAnnotation(annotation);
    }
    
    public void reorderAnnotations(List<Annotation> annotations) {
        saveStateForUndo();
        // Update order field and save all
        for (int i = 0; i < annotations.size(); i++) {
            annotations.get(i).order = i;
        }
        if (mCurrentPhoto.getValue() != null) {
            mRepository.replaceAnnotationsForPhoto(mCurrentPhoto.getValue().id, annotations);
        }
    }

    private void saveStateForUndo() {
        if (mAnnotations != null && mAnnotations.getValue() != null) {
            List<Annotation> snapshot = new ArrayList<>();
            for (Annotation a : mAnnotations.getValue()) {
                snapshot.add(new Annotation(a)); // Deep copy
            }
            undoStack.push(snapshot);
        }
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            List<Annotation> previousState = undoStack.pop();
            if (mCurrentPhoto.getValue() != null) {
                mRepository.replaceAnnotationsForPhoto(mCurrentPhoto.getValue().id, previousState);
            }
        }
    }
    
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
}
