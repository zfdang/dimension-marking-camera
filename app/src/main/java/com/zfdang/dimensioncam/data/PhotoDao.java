package com.zfdang.dimensioncam.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PhotoDao {
    @androidx.room.Transaction
    @Query("SELECT * FROM photos ORDER BY createdAt DESC")
    LiveData<List<PhotoWithAnnotations>> getPhotosWithAnnotations();

    @Query("SELECT * FROM photos ORDER BY createdAt DESC")
    LiveData<List<Photo>> getAllPhotos();

    @Insert
    long insert(Photo photo);

    @Delete
    void delete(Photo photo);
    
    @Query("SELECT * FROM photos WHERE id = :id")
    Photo getPhotoById(long id);
}
