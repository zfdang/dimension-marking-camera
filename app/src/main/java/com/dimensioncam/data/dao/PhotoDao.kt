package com.dimensioncam.data.dao

import androidx.room.*
import com.dimensioncam.data.model.Photo
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Photo entity
 */
@Dao
interface PhotoDao {
    
    @Query("SELECT * FROM photos ORDER BY modifiedAt DESC")
    fun getAllPhotos(): Flow<List<Photo>>
    
    @Query("SELECT * FROM photos WHERE id = :photoId")
    suspend fun getPhotoById(photoId: Long): Photo?
    
    @Query("SELECT * FROM photos WHERE id = :photoId")
    fun observePhotoById(photoId: Long): Flow<Photo?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: Photo): Long
    
    @Update
    suspend fun updatePhoto(photo: Photo)
    
    @Delete
    suspend fun deletePhoto(photo: Photo)
    
    @Query("DELETE FROM photos WHERE id = :photoId")
    suspend fun deletePhotoById(photoId: Long)
    
    @Query("UPDATE photos SET modifiedAt = :timestamp WHERE id = :photoId")
    suspend fun updateModifiedTime(photoId: Long, timestamp: Long = System.currentTimeMillis())
}
