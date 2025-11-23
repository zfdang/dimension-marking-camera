package com.zfdang.dimensioncam.ui.photos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.zfdang.dimensioncam.data.Photo;
import com.zfdang.dimensioncam.data.Repository;

import java.util.List;

import com.zfdang.dimensioncam.data.PhotoWithAnnotations;

public class PhotosViewModel extends AndroidViewModel {

    private Repository mRepository;
    private LiveData<List<PhotoWithAnnotations>> mAllPhotos;

    public PhotosViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
        mAllPhotos = mRepository.getPhotosWithAnnotations();
    }

    public LiveData<List<PhotoWithAnnotations>> getAllPhotos() {
        return mAllPhotos;
    }

    public void insert(Photo photo) {
        mRepository.insertPhoto(photo);
    }

    public void delete(Photo photo) {
        mRepository.deletePhoto(photo);
    }
}
