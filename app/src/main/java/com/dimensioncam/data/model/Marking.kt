package com.dimensioncam.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Marking entity representing a dimension annotation on a photo
 * Coordinates are normalized (0.0 to 1.0) relative to image dimensions
 */
@Entity(
    tableName = "markings",
    foreignKeys = [
        ForeignKey(
            entity = Photo::class,
            parentColumns = ["id"],
            childColumns = ["photoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("photoId")]
)
data class Marking(
    @PrimaryKey(autoGenerate = true) 
    val id: Long = 0,
    
    /** ID of the associated photo */
    val photoId: Long,
    
    /** Start point X coordinate (normalized 0.0-1.0) */
    val startX: Float,
    
    /** Start point Y coordinate (normalized 0.0-1.0) */
    val startY: Float,
    
    /** End point X coordinate (normalized 0.0-1.0) */
    val endX: Float,
    
    /** End point Y coordinate (normalized 0.0-1.0) */
    val endY: Float,
    
    /** Distance value entered by user */
    val distanceValue: Float,
    
    /** Distance unit (mm or cm) */
    val distanceUnit: DistanceUnit,
    
    /** Line color in ARGB format */
    val lineColor: Int,
    
    /** Line width in dp */
    val lineWidthDp: Float,
    
    /** Text color in ARGB format */
    val textColor: Int,
    
    /** Text size in sp */
    val textSizeSp: Float,
    
    /** Display order (higher values drawn on top) */
    val displayOrder: Int
)

/**
 * Distance unit enumeration
 */
enum class DistanceUnit {
    MM,  // Millimeters
    CM   // Centimeters
}
