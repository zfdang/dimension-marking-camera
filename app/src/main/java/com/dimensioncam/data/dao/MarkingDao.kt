package com.dimensioncam.data.dao

import androidx.room.*
import com.dimensioncam.data.model.Marking
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Marking entity
 */
@Dao
interface MarkingDao {
    
    @Query("SELECT * FROM markings WHERE photoId = :photoId ORDER BY displayOrder ASC")
    fun getMarkingsForPhoto(photoId: Long): Flow<List<Marking>>
    
    @Query("SELECT * FROM markings WHERE photoId = :photoId ORDER BY displayOrder ASC")
    suspend fun getMarkingsForPhotoSync(photoId: Long): List<Marking>
    
    @Query("SELECT * FROM markings WHERE id = :markingId")
    suspend fun getMarkingById(markingId: Long): Marking?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarking(marking: Marking): Long
    
    @Update
    suspend fun updateMarking(marking: Marking)
    
    @Update
    suspend fun updateMarkings(markings: List<Marking>)
    
    @Delete
    suspend fun deleteMarking(marking: Marking)
    
    @Query("DELETE FROM markings WHERE id = :markingId")
    suspend fun deleteMarkingById(markingId: Long)
    
    @Query("DELETE FROM markings WHERE photoId = :photoId")
    suspend fun deleteAllMarkingsForPhoto(photoId: Long)
    
    @Query("SELECT MAX(displayOrder) FROM markings WHERE photoId = :photoId")
    suspend fun getMaxDisplayOrder(photoId: Long): Int?
}
