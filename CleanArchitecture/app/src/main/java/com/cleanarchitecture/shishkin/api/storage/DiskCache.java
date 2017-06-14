package com.cleanarchitecture.shishkin.api.storage;

import android.content.Context;

import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.api.controller.IModule;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

import io.paperdb.Paper;

public class DiskCache implements IStorage, IModule {

    public static final String NAME = DiskCache.class.getName();
    private static final String LOG_TAG = "DiskCache:";

    private static volatile DiskCache sInstance;
    private ReentrantLock mLock;

    public static DiskCache getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (DiskCache.class) {
                if (sInstance == null) {
                    sInstance = new DiskCache(context);
                }
            }
        }
        return sInstance;
    }

    private DiskCache(final Context context) {
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
    public Serializable get(final String key) {
        if (StringUtils.isNullOrEmpty(key)) {
            return null;
        }

        mLock.lock();

        try {
            if (Paper.book(NAME).exist(key)) {
                return Paper.book(NAME).read(key);
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
            return Paper.book(NAME).read(key, defaultValue);
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            mLock.unlock();
        }
        return defaultValue;
    }

    @Override
    public void clear(final String key) {
        if (StringUtils.isNullOrEmpty(key)) {
            return;
        }

        mLock.lock();

        try {
            Paper.book(NAME).delete(key);
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void clearAll() {
        mLock.lock();

        try {
            Paper.book(NAME).destroy();
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
