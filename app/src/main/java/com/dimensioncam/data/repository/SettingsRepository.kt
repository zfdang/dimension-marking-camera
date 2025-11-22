package com.dimensioncam.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.dimensioncam.data.model.AppSettings
import com.dimensioncam.data.model.ArrowStyle
import com.dimensioncam.data.model.DistanceUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property for DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Repository for app settings using DataStore
 */
class SettingsRepository(private val context: Context) {
    
    private val dataStore: DataStore<Preferences> = context.dataStore
    
    companion object {
        private val ARROW_STYLE = stringPreferencesKey("arrow_style")
        private val DEFAULT_DISTANCE_UNIT = stringPreferencesKey("default_distance_unit")
        private val LANGUAGE_CODE = stringPreferencesKey("language_code")
        private val DEFAULT_LINE_COLOR = intPreferencesKey("default_line_color")
        private val DEFAULT_LINE_WIDTH_DP = floatPreferencesKey("default_line_width_dp")
        private val DEFAULT_TEXT_COLOR = intPreferencesKey("default_text_color")
        private val DEFAULT_TEXT_SIZE_SP = floatPreferencesKey("default_text_size_sp")
    }
    
    /**
     * Get settings as Flow
     */
    val settingsFlow: Flow<AppSettings> = dataStore.data.map { preferences ->
        AppSettings(
            arrowStyle = ArrowStyle.valueOf(
                preferences[ARROW_STYLE] ?: ArrowStyle.ARROW.name
            ),
            defaultDistanceUnit = DistanceUnit.valueOf(
                preferences[DEFAULT_DISTANCE_UNIT] ?: DistanceUnit.CM.name
            ),
            languageCode = preferences[LANGUAGE_CODE] ?: "auto",
            defaultLineColor = preferences[DEFAULT_LINE_COLOR] ?: android.graphics.Color.RED,
            defaultLineWidthDp = preferences[DEFAULT_LINE_WIDTH_DP] ?: 3f,
            defaultTextColor = preferences[DEFAULT_TEXT_COLOR] ?: android.graphics.Color.WHITE,
            defaultTextSizeSp = preferences[DEFAULT_TEXT_SIZE_SP] ?: 14f
        )
    }
    
    /**
     * Update arrow style
     */
    suspend fun updateArrowStyle(arrowStyle: ArrowStyle) {
        dataStore.edit { preferences ->
            preferences[ARROW_STYLE] = arrowStyle.name
        }
    }
    
    /**
     * Update default distance unit
     */
    suspend fun updateDefaultDistanceUnit(unit: DistanceUnit) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_DISTANCE_UNIT] = unit.name
        }
    }
    
    /**
     * Update language code
     */
    suspend fun updateLanguageCode(code: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_CODE] = code
        }
    }
    
    /**
     * Update default line color
     */
    suspend fun updateDefaultLineColor(color: Int) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_LINE_COLOR] = color
        }
    }
    
    /**
     * Update default line width
     */
    suspend fun updateDefaultLineWidthDp(width: Float) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_LINE_WIDTH_DP] = width
        }
    }
    
    /**
     * Update default text color
     */
    suspend fun updateDefaultTextColor(color: Int) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_TEXT_COLOR] = color
        }
    }
    
    /**
     * Update default text size
     */
    suspend fun updateDefaultTextSizeSp(size: Float) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_TEXT_SIZE_SP] = size
        }
    }
}
