package com.dimensioncam.data.model

import android.graphics.Color

/**
 * Settings data class for app preferences
 */
data class AppSettings(
    /** Global arrow style for all markings */
    val arrowStyle: ArrowStyle = ArrowStyle.ARROW,
    
    /** Default distance unit for new markings */
    val defaultDistanceUnit: DistanceUnit = DistanceUnit.CM,
    
    /** Language code: "auto", "en", "zh" */
    val languageCode: String = "auto",
    
    /** Default line color for new markings */
    val defaultLineColor: Int = Color.RED,
    
    /** Default line width for new markings (dp) */
    val defaultLineWidthDp: Float = 3f,
    
    /** Default text color for new markings */
    val defaultTextColor: Int = Color.WHITE,
    
    /** Default text size for new markings (sp) */
    val defaultTextSizeSp: Float = 14f
)
