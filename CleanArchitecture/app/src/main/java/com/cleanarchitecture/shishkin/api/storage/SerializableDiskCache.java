package com.cleanarchitecture.shishkin.api.storage;

import android.content.Context;

import com.cleanarchitecture.shishkin.api.controller.AbstractModule;
import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import io.paperdb.Paper;

public class SerializableDiskCache extends AbstractModule implements IExpiredSerializableStorage {

    public static final String NAME = SerializableDiskCache.class.getName();
    private static final String TIME = SerializableDiskCache.class.getName() + ".time";
    private static final String LOG_TAG = "SerializableDiskCache:";

    private static volatile SerializableDiskCache sInstance;
    private ReentrantLock mLock;

    public static SerializableDiskCache getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (SerializableDiskCache.class) {
                if (sInstance == null) {
                    sInstance = new SerializableDiskCache(context);
                }
            }
        }
        return sInstance;
    }

    private SerializableDiskCache(final Context context) {
        mLock = new ReentrantLock();

        if (context != null) {
            Paper.init(context);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void put(final String key, final Serializable value) {
        if (StringUtils.isNullOrEmpty(key)) {
            return;
        }

        mLock.lock();

        try {
            if (value == null) {
                Paper.book(NAME).delete(key);
            } else {
                Paper.book(NAME).write(key, value);
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void put(final String key, final Serializable value, final long expired) {
        if (StringUtils.isNullOrEmpty(key)) {
            return;
        }

        if (expired < System.currentTimeMillis()) {
            return;
        }

        mLock.lock();

        try {
            if (value == null) {
                deleteKeys(key);
            } else {
                Paper.book(NAME).write(key, value);
                Paper.book(TIME).write(key, expired);
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            mLock.unlock();
        }
    }

    private void deleteKeys(final String key) {
        if (Paper.book(NAME).exist(key)) {
            Paper.book(NAME).delete(key);
        }
        if (Paper.book(TIME).exist(key)) {
            Paper.book(TIME).delete(key);
        }
    }

    @Override
    public Serializable get(final String key) {
        if (StringUtils.isNullOrEmpty(key)) {
            return null;
        }

        mLock.lock();

        try {
            if (Paper.book(NAME).exist(key)) {
                if (Paper.book(TIME).exist(key)) {
                    final long expired = Paper.book(TIME).read(key);
                    if (expired < System.currentTimeMillis()) {
                        deleteKeys(key);
                        return null;
                    }
                }
                return Paper.book(NAME).read(key);
            } else {
                deleteKeys(key);
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
        final Serializable value = get(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public void clear(final String key) {
        if (StringUtils.isNullOrEmpty(key)) {
            return;
        }

        mLock.lock();

        try {
            deleteKeys(key);
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
            Paper.book(NAME).destroy();
            Paper.book(TIME).destroy();
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void check() {
        mLock.lock();

        try {
            final List<String> list = Paper.book(TIME).getAllKeys();
            for (String key : list) {
                long expired = Paper.book(TIME).read(key);
                if (expired < System.currentTimeMillis()) {
                    deleteKeys(key);
                }
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public String getSubscriberType() {
        return null;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }
}
