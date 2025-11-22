package com.dimensioncam.data.repository

import com.dimensioncam.data.dao.MarkingDao
import com.dimensioncam.data.dao.PhotoDao
import com.dimensioncam.data.model.Marking
import com.dimensioncam.data.model.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

/**
 * Repository for Photo and Marking data operations
 */
class PhotoRepository(
    private val photoDao: PhotoDao,
    private val markingDao: MarkingDao
) {
    
    // Photo operations
    
    fun getAllPhotos(): Flow<List<Photo>> = photoDao.getAllPhotos()
    
    suspend fun getPhotoById(photoId: Long): Photo? = photoDao.getPhotoById(photoId)
    
    fun observePhotoById(photoId: Long): Flow<Photo?> = photoDao.observePhotoById(photoId)
    
    suspend fun insertPhoto(photo: Photo): Long = photoDao.insertPhoto(photo)
    
    suspend fun updatePhoto(photo: Photo) = photoDao.updatePhoto(photo)
    
    suspend fun deletePhoto(photo: Photo) {
        photoDao.deletePhoto(photo)
    }
    
    suspend fun deletePhotoById(photoId: Long) {
        photoDao.deletePhotoById(photoId)
    }
    
    suspend fun updatePhotoModifiedTime(photoId: Long) {
        photoDao.updateModifiedTime(photoId)
    }
    
    // Marking operations
    
    fun getMarkingsForPhoto(photoId: Long): Flow<List<Marking>> = 
        markingDao.getMarkingsForPhoto(photoId)
    
    suspend fun getMarkingsForPhotoSync(photoId: Long): List<Marking> =
        markingDao.getMarkingsForPhotoSync(photoId)
    
    suspend fun getMarkingById(markingId: Long): Marking? = 
        markingDao.getMarkingById(markingId)
    
    suspend fun insertMarking(marking: Marking): Long {
        val markingId = markingDao.insertMarking(marking)
        // Update photo modified time
        updatePhotoModifiedTime(marking.photoId)
        return markingId
    }
    
    suspend fun updateMarking(marking: Marking) {
        markingDao.updateMarking(marking)
        updatePhotoModifiedTime(marking.photoId)
    }
    
    suspend fun updateMarkings(markings: List<Marking>) {
        markingDao.updateMarkings(markings)
        if (markings.isNotEmpty()) {
            updatePhotoModifiedTime(markings.first().photoId)
        }
    }
    
    suspend fun deleteMarking(marking: Marking) {
        markingDao.deleteMarking(marking)
        updatePhotoModifiedTime(marking.photoId)
    }
    
    suspend fun deleteMarkingById(markingId: Long) {
        val marking = markingDao.getMarkingById(markingId)
        if (marking != null) {
            markingDao.deleteMarkingById(markingId)
            updatePhotoModifiedTime(marking.photoId)
        }
    }
    
    suspend fun deleteAllMarkingsForPhoto(photoId: Long) {
        markingDao.deleteAllMarkingsForPhoto(photoId)
    }
    
    suspend fun getNextDisplayOrder(photoId: Long): Int {
        val maxOrder = markingDao.getMaxDisplayOrder(photoId) ?: -1
        return maxOrder + 1
    }
}
