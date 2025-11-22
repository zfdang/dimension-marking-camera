package com.dimensioncam.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Photo entity representing an image with markings
 */
@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey(autoGenerate = true) 
    val id: Long = 0,
    
    /** URI of the original photo (could be content:// or file:// URI) */
    val originalUri: String,
    
    /** Path to cached thumbnail image with rendered markings */
    val thumbnailPath: String = "",
    
    /** Timestamp when photo was added */
    val createdAt: Long = System.currentTimeMillis(),
    
    /** Timestamp when photo or markings were last modified */
    val modifiedAt: Long = System.currentTimeMillis()
)
