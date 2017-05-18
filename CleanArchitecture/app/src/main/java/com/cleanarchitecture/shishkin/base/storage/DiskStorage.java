package com.cleanarchitecture.shishkin.base.storage;

import android.content.Context;

import com.cleanarchitecture.shishkin.base.utils.StringUtils;
import com.github.snowdream.android.util.Log;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

import io.paperdb.Paper;

public class DiskStorage implements IStorage {

    public static String NAME = "DiskStorage";

    private static volatile DiskStorage sInstance;
    private ReentrantLock mLock;

    public static DiskStorage getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (DiskStorage.class) {
                if (sInstance == null) {
                    sInstance = new DiskStorage(context);
                }
            }
        }
        return sInstance;
    }

    private DiskStorage(final Context context) {
        mLock = new ReentrantLock();

        if (context != null) {
            Paper.init(context);
        } else {
            sInstance = null;
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void put(final String key, final Serializable value) {
        if(StringUtils.isNullOrEmpty(key)) {
            return;
        }

        mLock.lock();

        try {
            if (value == null)  {
                Paper.book(NAME).delete(key);
            } else {
                Paper.book(NAME).write(key, value);
            }
        } catch (Exception e) {
            Log.e(getName(), e.getMessage());
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
            if (Paper.book(NAME).exist(key)) {
                return Paper.book(NAME).read(key);
            }
        } catch (Exception e) {
            Log.e(getName(), e.getMessage());
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
            return Paper.book(NAME).read(key, defaultValue);
        } catch (Exception e) {
            Log.e(getName(), e.getMessage());
        } finally {
            mLock.unlock();
        }
        return defaultValue;
    }

    @Override
    public void clear(final String key) {
        if(StringUtils.isNullOrEmpty(key)) {
            return;
        }

        mLock.lock();

        try {
            Paper.book(NAME).delete(key);
        } catch (Exception e) {
            Log.e(getName(), e.getMessage());
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
            Log.e(getName(), e.getMessage());
        } finally {
            mLock.unlock();
        }
    }

}
