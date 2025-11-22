package com.dimensioncam.ui.marking

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dimensioncam.data.model.Marking
import com.dimensioncam.data.model.Photo
import com.dimensioncam.data.repository.PhotoRepository
import com.dimensioncam.data.repository.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for Marking screen
 */
class MarkingViewModel(
    private val photoRepository: PhotoRepository,
    private val settingsRepository: SettingsRepository,
    private val photoId: Long
) : ViewModel() {
    
    // Photo data
    val photo: StateFlow<Photo?> = photoRepository.observePhotoById(photoId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    
    // Markings for this photo
    val markings: StateFlow<List<Marking>> = photoRepository.getMarkingsForPhoto(photoId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Settings
    val settings = settingsRepository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = com.dimensioncam.data.model.AppSettings()
        )
    
    // UI State
    private val _selectedMarkingId = MutableStateFlow<Long?>(null)
    val selectedMarkingId: StateFlow<Long?> = _selectedMarkingId.asStateFlow()
    
    private val _isCreatingMarking = MutableStateFlow(false)
    val isCreatingMarking: StateFlow<Boolean> = _isCreatingMarking.asStateFlow()
    
    private val _newMarkingStart = MutableStateFlow<Pair<Float, Float>?>(null)
    val newMarkingStart: StateFlow<Pair<Float, Float>?> = _newMarkingStart.asStateFlow()
    
    private val _newMarkingEnd = MutableStateFlow<Pair<Float, Float>?>(null)
    val newMarkingEnd: StateFlow<Pair<Float, Float>?> = _newMarkingEnd.asStateFlow()
    
    private val _undoStack = MutableStateFlow<List<UndoAction>>(emptyList())
    val undoStack: StateFlow<List<UndoAction>> = _undoStack.asStateFlow()
    
    val selectedMarking: StateFlow<Marking?> = combine(
        selectedMarkingId,
        markings
    ) { id, marks ->
        id?.let { selectedId ->
            marks.find { it.id == selectedId }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    
    /**
     * Start creating a new marking
     */
    fun startCreatingMarking() {
        _isCreatingMarking.value = true
        _selectedMarkingId.value = null
    }
    
    /**
     * Set new marking start point
     */
    fun setNewMarkingStart(x: Float, y: Float) {
        _newMarkingStart.value = Pair(x, y)
        _newMarkingEnd.value = Pair(x, y)
    }
    
    /**
     * Update new marking end point
     */
    fun updateNewMarkingEnd(x: Float, y: Float) {
        _newMarkingEnd.value = Pair(x, y)
    }
    
    /**
     * Finish creating marking (save to database)
     */
    fun finishCreatingMarking(distanceValue: Float) {
        viewModelScope.launch {
            val start = _newMarkingStart.value
            val end = _newMarkingEnd.value
            
            if (start != null && end != null) {
                val nextOrder = photoRepository.getNextDisplayOrder(photoId)
                val currentSettings = settings.value
                
                val marking = Marking(
                    photoId = photoId,
                    startX = start.first,
                    startY = start.second,
                    endX = end.first,
                    endY = end.second,
                    distanceValue = distanceValue,
                    distanceUnit = currentSettings.defaultDistanceUnit,
                    lineColor = currentSettings.defaultLineColor,
                    lineWidthDp = currentSettings.defaultLineWidthDp,
                    textColor = currentSettings.defaultTextColor,
                    textSizeSp = currentSettings.defaultTextSizeSp,
                    displayOrder = nextOrder
                )
                
                val id = photoRepository.insertMarking(marking)
                
                // Add to undo stack
                _undoStack.value = _undoStack.value + UndoAction.Create(id)
                
                // Reset creation state
                cancelCreatingMarking()
            }
        }
    }
    
    /**
     * Cancel creating marking
     */
    fun cancelCreatingMarking() {
        _isCreatingMarking.value = false
        _newMarkingStart.value = null
        _newMarkingEnd.value = null
    }
    
    /**
     * Select a marking
     */
    fun selectMarking(markingId: Long?) {
        _selectedMarkingId.value = markingId
        if (markingId != null) {
            _isCreatingMarking.value = false
        }
    }
    
    /**
     * Update marking position
     */
    fun updateMarkingPosition(
        markingId: Long,
        startX: Float? = null,
        startY: Float? = null,
        endX: Float? = null,
        endY: Float? = null
    ) {
        viewModelScope.launch {
            val marking = photoRepository.getMarkingById(markingId) ?: return@launch
            
            val updated = marking.copy(
                startX = startX ?: marking.startX,
                startY = startY ?: marking.startY,
                endX = endX ?: marking.endX,
                endY = endY ?: marking.endY
            )
            
            photoRepository.updateMarking(updated)
        }
    }
    
    /**
     * Update marking properties
     */
    fun updateMarking(marking: Marking) {
        viewModelScope.launch {
            photoRepository.updateMarking(marking)
        }
    }
    
    /**
     * Delete marking
     */
    fun deleteMarking(markingId: Long) {
        viewModelScope.launch {
            val marking = photoRepository.getMarkingById(markingId)
            if (marking != null) {
                photoRepository.deleteMarking(marking)
                _undoStack.value = _undoStack.value + UndoAction.Delete(marking)
                
                if (_selectedMarkingId.value == markingId) {
                    _selectedMarkingId.value = null
                }
            }
        }
    }
    
    /**
     * Reorder markings
     */
    fun reorderMarkings(reorderedMarkings: List<Marking>) {
        viewModelScope.launch {
            val updatedMarkings = reorderedMarkings.mapIndexed { index, marking ->
                marking.copy(displayOrder = index)
            }
            photoRepository.updateMarkings(updatedMarkings)
        }
    }
    
    /**
     * Undo last action
     */
    fun undo() {
        viewModelScope.launch {
            val lastAction = _undoStack.value.lastOrNull() ?: return@launch
            
            when (lastAction) {
                is UndoAction.Create -> {
                    photoRepository.deleteMarkingById(lastAction.markingId)
                }
                is UndoAction.Delete -> {
                    photoRepository.insertMarking(lastAction.marking)
                }
            }
            
            _undoStack.value = _undoStack.value.dropLast(1)
        }
    }
    
    companion object {
        fun Factory(
            photoRepository: PhotoRepository,
            settingsRepository: SettingsRepository,
            photoId: Long
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MarkingViewModel(photoRepository, settingsRepository, photoId) as T
            }
        }
    }
}

/**
 * Undo action types
 */
sealed class UndoAction {
    data class Create(val markingId: Long) : UndoAction()
    data class Delete(val marking: Marking) : UndoAction()
}
