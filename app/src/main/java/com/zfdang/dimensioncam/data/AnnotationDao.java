package com.zfdang.dimensioncam.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AnnotationDao {
    @Query("SELECT * FROM annotations WHERE photoId = :photoId ORDER BY `order` ASC")
    LiveData<List<Annotation>> getAnnotationsForPhoto(long photoId);

    @Query("SELECT * FROM annotations WHERE photoId = :photoId ORDER BY `order` ASC")
    List<Annotation> getAnnotationsForPhotoSync(long photoId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Annotation annotation);

    @Update
    void update(Annotation annotation);

    @Delete
    void delete(Annotation annotation);

    @Query("DELETE FROM annotations WHERE photoId = :photoId")
    void deleteAllForPhoto(long photoId);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Annotation> annotations);
}
