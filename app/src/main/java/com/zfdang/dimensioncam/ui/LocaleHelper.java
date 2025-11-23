package com.zfdang.dimensioncam.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.zfdang.dimensioncam.ui.settings.SettingsManager;

import java.util.Locale;

public class LocaleHelper {

    public static Context onAttach(Context context) {
        String lang = new SettingsManager(context).getLanguage();
        return setLocale(context, lang);
    }

    public static Context setLocale(Context context, String language) {
        if ("auto".equals(language)) {
            return context;
        }
        return updateResources(context, language);
    }

    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        
        return context.createConfigurationContext(config);
    }
}
