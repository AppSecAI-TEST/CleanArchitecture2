package com.cleanarchitecture.shishkin.api.controller;

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
    public static final String DESKTOP = "desktop";
    public static final String IMAGE_CACHE_VERSION = "image_cache_version";
    public static final String PARCELABLE_CACHE_VERSION = "parcelable_cache_version";

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
     * Получить версию кэша картинок
     *
     * @param defaultValue значение по умолчанию
     * @return версия кэша картинок
     */
    public static int getImageCacheVersion(final Context context, final int defaultValue) {
        return getInt(context, IMAGE_CACHE_VERSION, defaultValue);
    }

    /**
     * Установить версию кэша картинок
     *
     * @param version версия кэша картинок
     */
    public static void setImageCacheVersion(final Context context, final int version) {
        putInt(context, IMAGE_CACHE_VERSION, version);
    }

    /**
     * Получить версию кэша Parcelable
     *
     * @param defaultValue значение по умолчанию
     * @return версия кэша картинок
     */
    public static int getParcelableDiskCacheVersion(final Context context, final int defaultValue) {
        return getInt(context, PARCELABLE_CACHE_VERSION, defaultValue);
    }

    /**
     * Установить версию кэша Parcelable
     *
     * @param version версия кэша Parcelable
     */
    public static void setParcelableDiskCacheVersion(final Context context, final int version) {
        putInt(context, PARCELABLE_CACHE_VERSION, version);
    }

    /**
     * Получить рабочий стол
     *
     * @return рабочий стол
     */
    public static String getDesktop(final Context context) {
        return getString(context, DESKTOP, "");
    }

    /**
     * Установить рабочий стол
     *
     * @param desktop рабочий стол
     */
    public static void setDesktop(final Context context, final String desktop) {
        putString(context, DESKTOP, desktop);
    }

    /**
     * Получить порядок рабочего стола
     *
     * @param name рабочий стол
     * @return порядок рабочего стола
     */
    public static String getDesktopOrder(final Context context, final String name, final String desktopOrder) {
        return getString(context, name, desktopOrder);
    }

    /**
     * Сохранить порядок рабочего стола
     *
     * @param name         рабочий стол
     * @param desktopOrder порядок рабочего стола
     */
    public static void setDesktopOrder(final Context context, final String name, final String desktopOrder) {
        putString(context, name, desktopOrder);
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

