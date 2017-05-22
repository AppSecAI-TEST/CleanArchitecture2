package com.cleanarchitecture.shishkin.base.repository;

import android.content.Context;
import android.util.SparseIntArray;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.controller.AppPreferences;
import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.event.repository.RepositoryRequestGetImageEvent;
import com.cleanarchitecture.shishkin.base.storage.DiskCache;
import com.cleanarchitecture.shishkin.base.storage.DiskCacheService;
import com.cleanarchitecture.shishkin.base.storage.MemoryCache;
import com.cleanarchitecture.shishkin.base.storage.MemoryCacheService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

public class Repository implements IRepository {
    public static final String NAME = "Repository";
    public static final int FROM_CONTENT_PROVIDER = 0;
    public static final int FROM_CACHE = 1;
    public static final int FROM_NETWORK = 2;

    public static final int USE_NO_CACHE = 0;
    public static final int USE_MEMORY_CACHE = 1;
    public static final int USE_DISK_CACHE = 2;
    public static final int USE_MEMORY_AND_DISK_CACHE = 3;
    public static final int USE_ONLY_MEMORY_CACHE = 4;
    public static final int USE_ONLY_DISK_CACHE = 5;
    public static final int USE_ONLY_CACHE = 6;

    private SparseIntArray mItemsCacheType;

    private int mDefaultCaching = USE_ONLY_MEMORY_CACHE;
    private static volatile Repository sInstance;

    public static void instantiate() {
        if (sInstance == null) {
            synchronized (Repository.class) {
                if (sInstance == null) {
                    sInstance = new Repository();
                }
            }
        }
    }

    public static Repository getInstance() {
        instantiate();
        return sInstance;
    }

    private Repository() {
        NetProvider.instantiate();
        ContentProvider.instantiate();

        mItemsCacheType = new SparseIntArray();
        final Context context = ApplicationController.getInstance();
        if (context != null) {
            mDefaultCaching = AppPreferences.getInstance().getDefaultCaching(context, USE_MEMORY_CACHE);
        }

        EventController.getInstance().register(this);
    }

    @Override
    public synchronized Serializable getFromCache(int key) {
        final Context context = ApplicationController.getInstance();
        if (context == null) {
            return null;
        }

        final int use = mItemsCacheType.get(key, mDefaultCaching);
        switch (use) {
            case USE_NO_CACHE:
                break;

            case USE_ONLY_MEMORY_CACHE:
            case USE_MEMORY_CACHE:
                return MemoryCache.getInstance().get(String.valueOf(key));

            case USE_ONLY_DISK_CACHE:
            case USE_DISK_CACHE:
                return DiskCache.getInstance(context).get(String.valueOf(key));

            case USE_ONLY_CACHE:
            case USE_MEMORY_AND_DISK_CACHE:
                Serializable ser = MemoryCache.getInstance().get(String.valueOf(key));
                if (ser == null) {
                    ser = DiskCache.getInstance(context).get(String.valueOf(key));
                }
                return ser;
        }
        return null;
    }

    @Override
    public synchronized void putToCache(int key, Serializable value) {
        final Context context = ApplicationController.getInstance();
        if (context == null) {
            return;
        }

        final int use = mItemsCacheType.get(key, mDefaultCaching);
        switch (use) {
            case USE_NO_CACHE:
                break;

            case USE_ONLY_MEMORY_CACHE:
            case USE_MEMORY_CACHE:
                MemoryCacheService.put(context, String.valueOf(key), value);
                break;

            case USE_ONLY_DISK_CACHE:
            case USE_DISK_CACHE:
                DiskCacheService.put(context, String.valueOf(key), value);
                break;

            case USE_ONLY_CACHE:
            case USE_MEMORY_AND_DISK_CACHE:
                MemoryCacheService.put(context, String.valueOf(key), value);
                DiskCacheService.put(context, String.valueOf(key), value);
                break;
        }
    }

    @Override
    public synchronized int getTypeCached(final int key) {
        if (mItemsCacheType.indexOfKey(key) >= 0) {
            return mItemsCacheType.get(key);
        }
        return mDefaultCaching;
    }

    @Override
    public synchronized Repository setTypeCached(int key, int cacheType) {
        mItemsCacheType.put(key, cacheType);
        return this;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void setDefaultCaching(final int defaultCaching) {
        mDefaultCaching = defaultCaching;

        final Context context = ApplicationController.getInstance();
        if (context == null) {
            return;
        }
        AppPreferences.getInstance().setDefaultCaching(context, defaultCaching);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRepositoryRequestGetImageEvent(final RepositoryRequestGetImageEvent event) {
        RepositoryNetProvider.requestGetImage(event);
    }


}
