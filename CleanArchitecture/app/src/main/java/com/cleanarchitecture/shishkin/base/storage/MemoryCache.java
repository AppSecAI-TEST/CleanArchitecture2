package com.cleanarchitecture.shishkin.base.storage;

import com.cleanarchitecture.shishkin.base.controller.ISubscriber;
import com.cleanarchitecture.shishkin.base.utils.StringUtils;
import com.github.snowdream.android.util.Log;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


public class MemoryCache implements ISubscriber, IStorage {
    public static final String NAME = "MemoryCache";
    private static final long MAX_SIZE = 100L;
    private static final long DURATION = 10L;
    private static final TimeUnit DURATION_TIMEUNIT = TimeUnit.MINUTES;

    private static volatile MemoryCache sInstance;
    private LoadingCache<String, Serializable> mCache;
    private Serializable mValue;
    private ReentrantLock mLock;

    public static MemoryCache getInstance() {
        if (sInstance == null) {
            synchronized (MemoryCache.class) {
                if (sInstance == null) {
                    sInstance = new MemoryCache();
                }
            }
        }
        return sInstance;
    }

    private MemoryCache() {
        mLock = new ReentrantLock();

        mCache = CacheBuilder.newBuilder()
                .maximumSize(MAX_SIZE)
                .expireAfterWrite(DURATION, DURATION_TIMEUNIT)
                .build(
                        new CacheLoader<String, Serializable>() {
                            public Serializable load(String key) {
                                return mValue;
                            }
                        });
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void put(final String key, final Serializable value) {
        if(StringUtils.isNullOrEmpty(key) || value == null) {
            return;
        }

        mLock.lock();

        try {
            mValue = value;
            mCache.put(key, value);
        } catch (Exception e) {
            Log.e(NAME, e.getMessage());
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public Serializable get(final String key) {
        if(StringUtils.isNullOrEmpty(key)) {
            return null;
        }

        mLock.lock();

        try {
            return mCache.getIfPresent(key);
        } catch (Exception e) {
            Log.e(NAME, e.getMessage());
        } finally {
            mLock.unlock();
        }
        return null;
    }

    @Override
    public Serializable get(final String key, final Serializable defaultValue) {
        if(StringUtils.isNullOrEmpty(key)) {
            return defaultValue;
        }

        mLock.lock();

        try {
            final Serializable value = mCache.getIfPresent(key);
            if (value == null) {
                return defaultValue;
            }
            return value;
        } catch (Exception e) {
            Log.e(NAME, e.getMessage());
        } finally {
            mLock.unlock();
        }
        return defaultValue;
    }

    @Override
    public void clear(String key) {
        if(StringUtils.isNullOrEmpty(key)) {
            return;
        }

        mLock.lock();

        try {
            mCache.invalidate(key);
        } catch (Exception e) {
            Log.e(NAME, e.getMessage());
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void clearAll() {
        mLock.lock();

        try {
            mCache.invalidateAll();
        } catch (Exception e) {
            Log.e(NAME, e.getMessage());
        } finally {
            mLock.unlock();
        }
    }
}
