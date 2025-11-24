package com.zfdang.dimensioncam.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class Repository {

    private PhotoDao mPhotoDao;
    private AnnotationDao mAnnotationDao;

    public Repository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mPhotoDao = db.photoDao();
        mAnnotationDao = db.annotationDao();
    }

    // Photo methods
    public LiveData<List<Photo>> getAllPhotos() {
        return mPhotoDao.getAllPhotos();
    }

    public LiveData<List<PhotoWithAnnotations>> getPhotosWithAnnotations() {
        return mPhotoDao.getPhotosWithAnnotations();
    }

    public void insertPhoto(Photo photo) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mPhotoDao.insert(photo);
        });
    }

    public void deletePhoto(Photo photo) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mPhotoDao.delete(photo);
        });
    }

    // Annotation methods
    public LiveData<List<Annotation>> getAnnotationsForPhoto(long photoId) {
        return mAnnotationDao.getAnnotationsForPhoto(photoId);
    }

    public void insertAnnotation(Annotation annotation) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mAnnotationDao.insert(annotation);
        });
    }

    public void updateAnnotation(Annotation annotation) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mAnnotationDao.update(annotation);
        });
    }

    public void deleteAnnotation(Annotation annotation) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mAnnotationDao.delete(annotation);
        });
    }

    // For Undo/Redo or bulk updates, we might need to replace all annotations for a
    // photo
    public void replaceAnnotationsForPhoto(long photoId, List<Annotation> annotations) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mAnnotationDao.deleteAllForPhoto(photoId);
            mAnnotationDao.insertAll(annotations);
        });
    }
}
