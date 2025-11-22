package com.dimensioncam.data.database

import androidx.room.TypeConverter
import com.dimensioncam.data.model.DistanceUnit

/**
 * Type converters for Room database
 */
class Converters {
    
    @TypeConverter
    fun fromDistanceUnit(unit: DistanceUnit): String {
        return unit.name
    }
    
    @TypeConverter
    fun toDistanceUnit(value: String): DistanceUnit {
        return DistanceUnit.valueOf(value)
    }
}
