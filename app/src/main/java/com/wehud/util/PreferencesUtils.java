package com.wehud.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * This class hosts an instance of {@link SharedPreferences} and is used
 * to retrieve the user's settings for the application.
 *
 * @author Olivier Gonçalves, WeHud, 2017
 */

public final class PreferencesUtils {
    private static SharedPreferences mInstance;

    private PreferencesUtils() {}

    public static synchronized SharedPreferences getInstance(Context context) {
        if (mInstance == null)
            mInstance = PreferenceManager.getDefaultSharedPreferences(context);

        return mInstance;
    }

    static String get(Context context, String key) {
        return getInstance(context).getString(key, null);
    }

    public static void put(Context context, String key, String value) {
        SharedPreferences.Editor editor = getInstance(context).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void remove(Context context, String key) {
        SharedPreferences.Editor editor = getInstance(context).edit();
        editor.remove(key);
        editor.apply();
    }

    public static void clear(Context context) {
        SharedPreferences.Editor editor = getInstance(context).edit();
        editor.clear();
        editor.apply();
    }
}
