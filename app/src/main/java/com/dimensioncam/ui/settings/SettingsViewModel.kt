package com.dimensioncam.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dimensioncam.data.model.ArrowStyle
import com.dimensioncam.data.model.DistanceUnit
import com.dimensioncam.data.repository.SettingsRepository
import com.dimensioncam.utils.LocaleManagerUtil
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for Settings screen
 */
class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    val settings = settingsRepository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = com.dimensioncam.data.model.AppSettings()
        )
    
    fun updateArrowStyle(arrowStyle: ArrowStyle) {
        viewModelScope.launch {
            settingsRepository.updateArrowStyle(arrowStyle)
        }
    }
    
    fun updateDefaultDistanceUnit(unit: DistanceUnit) {
        viewModelScope.launch {
            settingsRepository.updateDefaultDistanceUnit(unit)
        }
    }
    
    fun updateLanguageCode(code: String, context: Context) {
        viewModelScope.launch {
            settingsRepository.updateLanguageCode(code)
            LocaleManagerUtil.applyLanguage(context, code)
        }
    }
    
    companion object {
        fun Factory(settingsRepository: SettingsRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(settingsRepository) as T
                }
            }
    }
}
