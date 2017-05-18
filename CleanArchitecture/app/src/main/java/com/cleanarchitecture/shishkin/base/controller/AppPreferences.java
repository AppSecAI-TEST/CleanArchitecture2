package com.cleanarchitecture.shishkin.base.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.cleanarchitecture.shishkin.R;

import java.util.Map;

/**
 * Preferences приложения
 */
@SuppressWarnings("unused")
public class AppPreferences {
    public static final String VERSION_APPLICATION = "version_application";

    private static volatile AppPreferences sInstance;

    /**
     * Получить Preferences приложения.
     *
     * @return Preferences приложения
     */
    public static AppPreferences getInstance() {
        if (sInstance == null) {
            synchronized (AppPreferences.class) {
                if (sInstance == null) {
                    sInstance = new AppPreferences();
                }
            }
        }
        return sInstance;
    }

    private AppPreferences() {
    }

    /**
     * Получить версию приложения.
     *
     * @return версия приложения
     */
    public String getApplicationVersion(final Context context) {
        return getString(context, VERSION_APPLICATION, null);
    }

    /**
     * Установить версию приложения.
     *
     * @param version версия приложения
     */
    public void setApplicationVersion(final Context context, final String version) {
        putString(context, VERSION_APPLICATION, version);
    }

    /**
     * Получить тип кеширования по умолчанию.
     *
     * @return тип кеширования
     */
    public int getDefaultCaching(final Context context, final int defaultValue) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            return settings.getInt(context.getString(R.string.setting_caching_type_key), defaultValue);
        }
        return defaultValue;
    }

    /**
     * Установить тип кеширования по умолчанию.
     *
     * @param defaultCaching тип кеширования
     */
    public void setDefaultCaching(final Context context, final int defaultCaching) {
        putInt(context, context.getString(R.string.setting_caching_type_key), defaultCaching);
    }

    public synchronized void putString(final Context context, final String key, final String value) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = settings.edit();
            editor.putString(key, value).commit();
        }
    }

    public synchronized String getString(final Context context, final String key) {
        return getString(context, key, null);
    }

    public synchronized String getString(final Context context, final String key, final String defaultValue) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            return settings.getString(key, defaultValue);
        }
        return defaultValue;
    }

    public synchronized void putInt(final Context context, final String key, final int value) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = settings.edit();
            editor.putInt(key, value).commit();
        }
    }

    public synchronized int getInt(final Context context, final String key) {
        return getInt(context, key, -1);
    }

    public synchronized int getInt(final Context context, final String key, final int defaultValue) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            return settings.getInt(key, defaultValue);
        }
        return defaultValue;
    }

    public synchronized void putLong(final Context context, final String key, final long value) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = settings.edit();
            editor.putLong(key, value).commit();
        }
    }

    public synchronized long getLong(final Context context, final String key) {
        return getLong(context, key, -1L);
    }

    public synchronized long getLong(final Context context, final String key, final long defaultValue) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            return settings.getLong(key, defaultValue);
        }
        return defaultValue;
    }

    public synchronized void putFloat(final Context context, final String key, final float value) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = settings.edit();
            editor.putFloat(key, value).commit();
        }
    }

    public synchronized float getFloat(final Context context, final String key) {
        return getFloat(context, key, -1f);
    }

    public float getFloat(final Context context, String key, float defaultValue) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            return settings.getFloat(key, defaultValue);
        }
        return defaultValue;
    }

    public synchronized void putBoolean(final Context context, final String key, final boolean value) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(key, value).commit();
        }
    }

    public synchronized boolean getBoolean(final Context context, final String key) {
        return getBoolean(context, key, false);
    }

    public synchronized boolean getBoolean(final Context context, final String key, final boolean defaultValue) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            return settings.getBoolean(key, defaultValue);
        }
        return defaultValue;
    }

    public synchronized Map<String, ?> getAll(final Context context) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            return settings.getAll();
        }
        return null;
    }

    public synchronized void remove(final Context context, final String key) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = settings.edit();
            editor.remove(key).commit();
        }
    }

    public synchronized boolean contains(final Context context, final String key) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            return settings.contains(key);
        }
        return false;
    }

    public synchronized void clear(final Context context) {
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
    public synchronized boolean checkPermission(final Context context, final String permission){
        return getInt(context, permission, -100) == PackageManager.PERMISSION_GRANTED;
    }
}

