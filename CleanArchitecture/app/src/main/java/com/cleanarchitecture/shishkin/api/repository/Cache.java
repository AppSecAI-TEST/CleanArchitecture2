package com.cleanarchitecture.shishkin.api.repository;

import android.content.Context;
import android.os.Parcelable;

import com.cleanarchitecture.shishkin.api.controller.Admin;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.service.ParcelableDiskCacheService;
import com.cleanarchitecture.shishkin.api.service.SerializableDiskCacheService;
import com.cleanarchitecture.shishkin.api.storage.IExpiredParcelableStorage;
import com.cleanarchitecture.shishkin.api.storage.IParcelableStorage;
import com.cleanarchitecture.shishkin.api.storage.ISerializableStorage;
import com.cleanarchitecture.shishkin.api.storage.ParcelableDiskCache;
import com.cleanarchitecture.shishkin.api.storage.ParcelableMemoryCache;
import com.cleanarchitecture.shishkin.api.storage.SerializableDiskCache;
import com.cleanarchitecture.shishkin.api.storage.SerializableMemoryCache;

import java.io.Serializable;
import java.util.List;

public class Cache {
    // типы кеширования
    public static final int USE_NO_CACHE = 0; // не использовать кеш ни при чтении ни при сохранении данных
    public static final int USE_MEMORY_CACHE = 1; // использовать кеш в памяти при чтении и при сохранении данных - данные будут прочитаны позднее
    public static final int USE_DISK_CACHE = 2; // использовать кеш на диске при чтении и при сохранении данных - данные будут прочитаны позднее
    public static final int USE_CACHE = 3; // использовать кеш в памяти и на диске при чтении и при сохранении данных - данные будут прочитаны позднее
    public static final int USE_ONLY_MEMORY_CACHE = 4; // использовать только кеш в памяти для получения данных
    public static final int USE_ONLY_DISK_CACHE = 5; // использовать только кеш на диске для получения данных
    public static final int USE_ONLY_CACHE = 6; // использовать только кеш в памяти и на диске для получения данных
    public static final int USE_SAVE_MEMORY_CACHE = 7; // сохранять только в кеше памяти после получения данных. Не использовать кеш для чтения
    public static final int USE_SAVE_DISK_CACHE = 8; // сохранять только в кеше на диске после получения данных. Не использовать кеш для чтения
    public static final int USE_SAVE_CACHE = 9; // сохранять в кеш в памяти и на диске после получения данных. Не использовать кеш для чтения

    public static Serializable get(final String key, final int cacheType) {
        final ISerializableStorage diskCache = Admin.getInstance().get(SerializableDiskCache.NAME);
        final ISerializableStorage memoryCache = Admin.getInstance().get(SerializableMemoryCache.NAME);

        switch (cacheType) {
            case USE_NO_CACHE:
                break;

            case USE_ONLY_MEMORY_CACHE:
            case USE_MEMORY_CACHE:
                if (memoryCache != null) {
                    return memoryCache.get(key);
                }
                break;

            case USE_ONLY_DISK_CACHE:
            case USE_DISK_CACHE:
                if (diskCache != null) {
                    return diskCache.get(key);
                }
                break;

            case USE_ONLY_CACHE:
            case USE_CACHE:
                Serializable value = null;
                if (memoryCache != null) {
                    value = memoryCache.get(key);
                }
                if (value == null) {
                    if (diskCache != null) {
                        value = diskCache.get(key);
                    }
                }
                return value;
        }
        return null;
    }

    public static <T extends Parcelable> T get(final String key, final int cacheType, final Class itemClass) {
        final IExpiredParcelableStorage<T> diskCache = Admin.getInstance().get(ParcelableDiskCache.NAME);
        final IParcelableStorage<T> memoryCache = Admin.getInstance().get(ParcelableMemoryCache.NAME);

        switch (cacheType) {
            case USE_NO_CACHE:
                break;

            case USE_ONLY_MEMORY_CACHE:
            case USE_MEMORY_CACHE:
                if (memoryCache != null) {
                    return memoryCache.get(key, itemClass);
                }
                break;

            case USE_ONLY_DISK_CACHE:
            case USE_DISK_CACHE:
                if (diskCache != null) {
                    return diskCache.get(key, itemClass);
                }
                break;

            case USE_ONLY_CACHE:
            case USE_CACHE:
                T value = null;
                if (memoryCache != null) {
                    value = memoryCache.get(key, itemClass);
                }
                if (value == null) {
                    if (diskCache != null) {
                        value = diskCache.get(key, itemClass);
                    }
                }
                return value;
        }
        return null;
    }

    public static <T extends Parcelable> List<T> getList(final String key, final int cacheType, final Class itemClass) {
        final IExpiredParcelableStorage<T> diskCache = Admin.getInstance().get(ParcelableDiskCache.NAME);
        final IParcelableStorage<T> memoryCache = Admin.getInstance().get(ParcelableMemoryCache.NAME);

        switch (cacheType) {
            case USE_NO_CACHE:
                break;

            case USE_ONLY_MEMORY_CACHE:
            case USE_MEMORY_CACHE:
                if (memoryCache != null) {
                    return memoryCache.getList(key, itemClass);
                }
                break;

            case USE_ONLY_DISK_CACHE:
            case USE_DISK_CACHE:
                if (diskCache != null) {
                    return diskCache.getList(key, itemClass);
                }
                break;

            case USE_ONLY_CACHE:
            case USE_CACHE:
                List<T> value = null;
                if (memoryCache != null) {
                    value = memoryCache.getList(key, itemClass);
                }
                if (value == null) {
                    if (diskCache != null) {
                        value = diskCache.getList(key, itemClass);
                    }
                }
                return value;
        }
        return null;
    }

    public static void put(final String key, final int cacheType, Serializable value, long expired) {
        final ISerializableStorage memoryCache = Admin.getInstance().get(SerializableMemoryCache.NAME);
        final Context context = AdminUtils.getContext();
        if (context == null) {
            return;
        }

        switch (cacheType) {
            case USE_NO_CACHE:
                break;

            case USE_ONLY_MEMORY_CACHE:
            case USE_SAVE_MEMORY_CACHE:
            case USE_MEMORY_CACHE:
                if (memoryCache != null) {
                    memoryCache.put(key, value);
                }
                break;

            case USE_ONLY_DISK_CACHE:
            case USE_SAVE_DISK_CACHE:
            case USE_DISK_CACHE:
                if (expired > 0) {
                    SerializableDiskCacheService.put(context, key, value, expired);
                } else {
                    SerializableDiskCacheService.put(context, key, value);
                }
                break;

            case USE_ONLY_CACHE:
            case USE_SAVE_CACHE:
            case USE_CACHE:
                if (memoryCache != null) {
                    memoryCache.put(key, value);
                }
                if (expired > 0) {
                    SerializableDiskCacheService.put(context, key, value, expired);
                } else {
                    SerializableDiskCacheService.put(context, key, value);
                }
                break;
        }
    }

    public static <T extends Parcelable> void put(final String key, final int cacheType, T value, long expired) {
        final IParcelableStorage<T> memoryCache = Admin.getInstance().get(ParcelableMemoryCache.NAME);
        final Context context = AdminUtils.getContext();
        if (context == null) {
            return;
        }

        switch (cacheType) {
            case USE_NO_CACHE:
                break;

            case USE_ONLY_MEMORY_CACHE:
            case USE_SAVE_MEMORY_CACHE:
            case USE_MEMORY_CACHE:
                if (memoryCache != null) {
                    memoryCache.put(key, value);
                }
                break;

            case USE_ONLY_DISK_CACHE:
            case USE_SAVE_DISK_CACHE:
            case USE_DISK_CACHE:
                if (expired > 0) {
                    ParcelableDiskCacheService.put(context, key, value, expired);
                } else {
                    ParcelableDiskCacheService.put(context, key, value);
                }
                break;

            case USE_ONLY_CACHE:
            case USE_SAVE_CACHE:
            case USE_CACHE:
                if (memoryCache != null) {
                    memoryCache.put(key, value);
                }
                if (expired > 0) {
                    ParcelableDiskCacheService.put(context, key, value, expired);
                } else {
                    ParcelableDiskCacheService.put(context, key, value);
                }
                break;
        }
    }

    public static <T extends Parcelable> void put(final String key, final int cacheType, List<T> value, long expired) {
        final IParcelableStorage<T> memoryCache = Admin.getInstance().get(ParcelableMemoryCache.NAME);
        final Context context = AdminUtils.getContext();
        if (context == null) {
            return;
        }

        switch (cacheType) {
            case USE_NO_CACHE:
                break;

            case USE_ONLY_MEMORY_CACHE:
            case USE_SAVE_MEMORY_CACHE:
            case USE_MEMORY_CACHE:
                if (memoryCache != null) {
                    memoryCache.put(key, value);
                }
                break;

            case USE_ONLY_DISK_CACHE:
            case USE_SAVE_DISK_CACHE:
            case USE_DISK_CACHE:
                if (expired > 0) {
                    ParcelableDiskCacheService.put(context, key, value, expired);
                } else {
                    ParcelableDiskCacheService.put(context, key, value);
                }
                break;

            case USE_ONLY_CACHE:
            case USE_SAVE_CACHE:
            case USE_CACHE:
                if (memoryCache != null) {
                    memoryCache.put(key, value);
                }
                if (expired > 0) {
                    ParcelableDiskCacheService.put(context, key, value, expired);
                } else {
                    ParcelableDiskCacheService.put(context, key, value);
                }
                break;
        }
    }

}
