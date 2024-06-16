package com.example.mystore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;


import java.util.Locale;

public class LanguageUtils {
    public static final String DEFAULT_LANGUAGE_CODE = "zh"; // 默认语言代码，例如中文
    public static final String LANGUAGE_PREFS = "LanguagePrefs";
    public static final String LANGUAGE_KEY = "language";

    public static String getSavedLanguage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE_CODE);
    }

    public static void saveLanguage(Context context, String languageCode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LANGUAGE_KEY, languageCode);
        editor.apply();
    }
}
