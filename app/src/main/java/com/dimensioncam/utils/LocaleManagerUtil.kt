package com.dimensioncam.utils

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.*

/**
 * Utility class for managing app language/locale
 */
object LocaleManagerUtil {
    
    /**
     * Apply language setting
     * 
     * @param context Application context
     * @param languageCode Language code: "auto", "en", "zh"
     */
    fun applyLanguage(context: Context, languageCode: String) {
        when (languageCode) {
            "auto" -> {
                // Use system default
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.getSystemService(LocaleManager::class.java)
                        ?.applicationLocales = LocaleList.getEmptyLocaleList()
                } else {
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
                }
            }
            "en" -> {
                setLocale(context, Locale.ENGLISH)
            }
            "zh" -> {
                setLocale(context, Locale.SIMPLIFIED_CHINESE)
            }
        }
    }
    
    /**
     * Set specific locale
     */
    private fun setLocale(context: Context, locale: Locale) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java)
                ?.applicationLocales = LocaleList(locale)
        } else {
            val localeList = LocaleListCompat.create(locale)
            AppCompatDelegate.setApplicationLocales(localeList)
        }
    }
    
    /**
     * Get current language code
     */
    fun getCurrentLanguageCode(): String {
        val locales = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            AppCompatDelegate.getApplicationLocales()
        } else {
            LocaleListCompat.getEmptyLocaleList()
        }
        
        return if (locales.isEmpty) {
            "auto"
        } else {
            when (locales[0]?.language) {
                "zh" -> "zh"
                "en" -> "en"
                else -> "auto"
            }
        }
    }
}
