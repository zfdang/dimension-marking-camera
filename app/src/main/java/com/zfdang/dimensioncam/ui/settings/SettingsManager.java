package com.zfdang.dimensioncam.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {
    private static final String PREF_NAME = "dimension_cam_prefs";
    private static final String KEY_ARROW_STYLE = "arrow_style";
    private static final String KEY_LANGUAGE = "language";

    public static final int STYLE_T_ARROW_T = 0; // |<----->| T型箭头T型
    public static final int STYLE_T_T = 1; // |-----| T型T型
    public static final int STYLE_ARROW_ARROW = 2; // <-----> 箭头箭头

    private SharedPreferences prefs;

    public SettingsManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public int getArrowStyle() {
        return prefs.getInt(KEY_ARROW_STYLE, STYLE_T_ARROW_T);
    }

    public void setArrowStyle(int style) {
        prefs.edit().putInt(KEY_ARROW_STYLE, style).apply();
    }

    // Language handling usually requires app restart or activity recreation
    // For simplicity, we'll just store the pref here.
    public String getLanguage() {
        return prefs.getString(KEY_LANGUAGE, "auto");
    }

    public void setLanguage(String lang) {
        prefs.edit().putString(KEY_LANGUAGE, lang).apply();
    }
}
