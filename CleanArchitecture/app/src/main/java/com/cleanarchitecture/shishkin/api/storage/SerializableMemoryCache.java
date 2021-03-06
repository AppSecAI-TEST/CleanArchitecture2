package com.cleanarchitecture.shishkin.api.storage;

import com.cleanarchitecture.shishkin.api.controller.AbstractModule;
import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.common.utils.SerializableUtil;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


public class SerializableMemoryCache extends AbstractModule implements ISerializableStorage {
    public static final String NAME = SerializableMemoryCache.class.getName();
    private static final String LOG_TAG = "SerializableMemoryCache:";
    private static final long MAX_SIZE = 1000L;
    private static final long DURATION = 3;
    private static final TimeUnit DURATION_TIMEUNIT = TimeUnit.MINUTES;

    private static volatile SerializableMemoryCache sInstance;
    private LoadingCache<String, Serializable> mCache;
    private Serializable mValue;
    private ReentrantLock mLock;

    public static SerializableMemoryCache getInstance() {
        if (sInstance == null) {
            synchronized (SerializableMemoryCache.class) {
                if (sInstance == null) {
                    sInstance = new SerializableMemoryCache();
                }
            }
        }
        return sInstance;
    }

    private SerializableMemoryCache() {
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
        if (StringUtils.isNullOrEmpty(key) || value == null) {
            return;
        }

        mLock.lock();

        try {
            if (validate()) {
                mValue = value;
                mCache.put(key, value);
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void put(final String key, final List<Serializable> values) {
        if (StringUtils.isNullOrEmpty(key) || values == null) {
            return;
        }
        mLock.lock();

        try {
            if (validate()) {
                final Serializable s = SerializableUtil.toSerializable(values);
                if (s != null) {
                    mValue = s;
                    mCache.put(key, s);
                }
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public Serializable get(final String key) {
        if (StringUtils.isNullOrEmpty(key)) {
            return null;
        }

        mLock.lock();

        try {
            return mCache.getIfPresent(key);
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            mLock.unlock();
        }
        return null;
    }

    @Override
    public List<Serializable> getList(final String key) {
        if (StringUtils.isNullOrEmpty(key)) {
            return null;
        }

        mLock.lock();

        try {
            final Serializable s = mCache.getIfPresent(key);
            if (s != null) {
                return SerializableUtil.serializableToList(s);
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            mLock.unlock();
        }
        return null;
    }

    @Override
    public Serializable get(final String key, final Serializable defaultValue) {
        if (StringUtils.isNullOrEmpty(key)) {
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
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            mLock.unlock();
        }
        return defaultValue;
    }

    @Override
    public void clear(String key) {
        if (StringUtils.isNullOrEmpty(key)) {
            return;
        }

        mLock.lock();

        try {
            mCache.invalidate(key);
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void clear() {
        mLock.lock();

        try {
            mCache.invalidateAll();
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            mLock.unlock();
        }
    }

    private boolean validate() {
        final Runtime runtime = Runtime.getRuntime();
        final long procent = 100 - ((runtime.totalMemory() - runtime.freeMemory()) * 100 / runtime.maxMemory());
        return procent >= 15;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Serializable memory cache";
    }
}
