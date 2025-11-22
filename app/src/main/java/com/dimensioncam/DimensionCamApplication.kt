package com.dimensioncam

import android.app.Application
import com.dimensioncam.data.database.AppDatabase
import com.dimensioncam.data.repository.PhotoRepository
import com.dimensioncam.data.repository.SettingsRepository

/**
 * Application class for DimensionCam
 */
class DimensionCamApplication : Application() {
    
    // Database instance
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    
    // Repositories
    val photoRepository: PhotoRepository by lazy {
        PhotoRepository(database.photoDao(), database.markingDao())
    }
    
    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(this)
    }
    
    override fun onCreate() {
        super.onCreate()
    }
}
