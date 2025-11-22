package com.dimensioncam.ui.photos

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dimensioncam.data.model.Photo
import com.dimensioncam.data.repository.PhotoRepository
import com.dimensioncam.data.repository.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for Photos screen
 */
class PhotosViewModel(
    private val photoRepository: PhotoRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    // Photos list from database
    val photos: StateFlow<List<Photo>> = photoRepository.getAllPhotos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Settings for export
    val settings = settingsRepository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = com.dimensioncam.data.model.AppSettings()
        )
    
    private val _exportStatus = MutableStateFlow<ExportStatus>(ExportStatus.Idle)
    val exportStatus: StateFlow<ExportStatus> = _exportStatus.asStateFlow()
    
    /**
     * Add a new photo
     */
    fun addPhoto(uri: Uri) {
        viewModelScope.launch {
            try {
                val photo = Photo(
                    originalUri = uri.toString(),
                    thumbnailPath = ""  // Will be generated when needed
                )
                photoRepository.insertPhoto(photo)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Delete a photo
     */
    fun deletePhoto(photo: Photo) {
        viewModelScope.launch {
            try {
                photoRepository.deletePhoto(photo)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Export photo with markings
     */
    fun exportPhoto(photoId: Long) {
        viewModelScope.launch {
            _exportStatus.value = ExportStatus.Exporting
            // Export functionality will be implemented in the UI layer
            // This is just a status holder
        }
    }
    
    fun resetExportStatus() {
        _exportStatus.value = ExportStatus.Idle
    }
    
    companion object {
        fun Factory(
            photoRepository: PhotoRepository,
            settingsRepository: SettingsRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PhotosViewModel(photoRepository, settingsRepository) as T
            }
        }
    }
}

sealed class ExportStatus {
    object Idle : ExportStatus()
    object Exporting : ExportStatus()
    data class Success(val uri: Uri) : ExportStatus()
    data class Error(val message: String) : ExportStatus()
}
