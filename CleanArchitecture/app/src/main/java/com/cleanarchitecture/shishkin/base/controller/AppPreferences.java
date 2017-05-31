package com.cleanarchitecture.shishkin.base.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import java.util.Map;

/**
 * Preferences приложения
 */
@SuppressWarnings("unused")
public class AppPreferences {
    public static final String VERSION_APPLICATION = "version_application";
    public static final String LAST_DAY_START = "last_day_start";

    /**
     * Получить версию приложения.
     *
     * @return версия приложения
     */
    public static String getApplicationVersion(final Context context) {
        return getString(context, VERSION_APPLICATION, null);
    }

    /**
     * Установить версию приложения.
     *
     * @param version версия приложения
     */
    public static void setApplicationVersion(final Context context, final String version) {
        putString(context, VERSION_APPLICATION, version);
    }

    /**
     * Получить последний день старта приложения
     *
     * @return последний день старта приложения
     */
    public static String getLastDayStart(final Context context) {
        return getString(context, LAST_DAY_START, null);
    }

    /**
     * Установить последний день старта приложения.
     *
     * @param day версия приложения
     */
    public static void setLastDayStart(final Context context, final String day) {
        putString(context, LAST_DAY_START, day);
    }

    public static void putString(final Context context, final String key, final String value) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = settings.edit();
            editor.putString(key, value).commit();
        }
    }

    public static String getString(final Context context, final String key) {
        return getString(context, key, null);
    }

    public static String getString(final Context context, final String key, final String defaultValue) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            return settings.getString(key, defaultValue);
        }
        return defaultValue;
    }

    public static void putInt(final Context context, final String key, final int value) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = settings.edit();
            editor.putInt(key, value).commit();
        }
    }

    public static int getInt(final Context context, final String key) {
        return getInt(context, key, -1);
    }

    public static int getInt(final Context context, final String key, final int defaultValue) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            return settings.getInt(key, defaultValue);
        }
        return defaultValue;
    }

    public static void putLong(final Context context, final String key, final long value) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = settings.edit();
            editor.putLong(key, value).commit();
        }
    }

    public static long getLong(final Context context, final String key) {
        return getLong(context, key, -1L);
    }

    public static long getLong(final Context context, final String key, final long defaultValue) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            return settings.getLong(key, defaultValue);
        }
        return defaultValue;
    }

    public static void putFloat(final Context context, final String key, final float value) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = settings.edit();
            editor.putFloat(key, value).commit();
        }
    }

    public static float getFloat(final Context context, final String key) {
        return getFloat(context, key, -1f);
    }

    public static float getFloat(final Context context, String key, float defaultValue) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            return settings.getFloat(key, defaultValue);
        }
        return defaultValue;
    }

    public static void putBoolean(final Context context, final String key, final boolean value) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(key, value).commit();
        }
    }

    public static boolean getBoolean(final Context context, final String key) {
        return getBoolean(context, key, false);
    }

    public static boolean getBoolean(final Context context, final String key, final boolean defaultValue) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            return settings.getBoolean(key, defaultValue);
        }
        return defaultValue;
    }

    public static Map<String, ?> getAll(final Context context) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            return settings.getAll();
        }
        return null;
    }

    public static void remove(final Context context, final String key) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = settings.edit();
            editor.remove(key).commit();
        }
    }

    public static boolean contains(final Context context, final String key) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            return settings.contains(key);
        }
        return false;
    }

    public static void clear(final Context context) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = settings.edit();
            editor.clear().commit();
        }
    }

    /**
     * Проверить установленное разрешение прав приложения.
     *
     * @param permission право приложения
     * @return если true разрешение установлено, false - разрешение не установлено
     */
    public static boolean checkPermission(final Context context, final String permission) {
        return getInt(context, permission, -100) == PackageManager.PERMISSION_GRANTED;
    }
}

