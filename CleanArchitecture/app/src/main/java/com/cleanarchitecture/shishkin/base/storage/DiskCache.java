package com.cleanarchitecture.shishkin.base.storage;

import android.content.Context;

import com.cleanarchitecture.shishkin.base.utils.StringUtils;
import com.github.snowdream.android.util.Log;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

import io.paperdb.Paper;

public class DiskCache implements IStorage {

    public static String NAME = "DiskCache";

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
            if (value == null) {
                Paper.book().delete(key);
            } else {
                Paper.book().write(key, value);
            }
        } catch (Exception e) {
            Log.e(getName(), e.getMessage());
        } finally {
            mLock.unlock();
        }
    }

    public void put(final String book, final String key, final Serializable value) {
        if(StringUtils.isNullOrEmpty(key)) {
            return;
        }

        if(StringUtils.isNullOrEmpty(book)) {
            return;
        }

        mLock.lock();

        try {
            if (value == null) {
                Paper.book(book).delete(key);
            } else {
                Paper.book(book).write(key, value);
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
            if (Paper.book().exist(key)) {
                return Paper.book().read(key);
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
            return Paper.book().read(key, defaultValue);
        } catch (Exception e) {
            Log.e(getName(), e.getMessage());
        } finally {
            mLock.unlock();
        }
        return defaultValue;
    }

    public Serializable get(final String book, final String key, final Serializable defaultValue) {
        if(StringUtils.isNullOrEmpty(book)) {
            return defaultValue;
        }

        if(StringUtils.isNullOrEmpty(key)) {
            return defaultValue;
        }

        mLock.lock();

        try {
            return Paper.book(book).read(key, defaultValue);
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
            Paper.book().delete(key);
        } catch (Exception e) {
            Log.e(getName(), e.getMessage());
        } finally {
            mLock.unlock();
        }
    }

    public void clear(final String book, final String key) {
        if(StringUtils.isNullOrEmpty(key)) {
            return;
        }

        if(StringUtils.isNullOrEmpty(book)) {
            return;
        }

        mLock.lock();

        try {
            Paper.book(book).delete(key);
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
            Paper.book().destroy();
        } catch (Exception e) {
            Log.e(getName(), e.getMessage());
        } finally {
            mLock.unlock();
        }
    }

    public void clearAll(final String book) {
        if(StringUtils.isNullOrEmpty(book)) {
            return;
        }

        mLock.lock();

        try {
            Paper.book(book).destroy();
        } catch (Exception e) {
            Log.e(getName(), e.getMessage());
        } finally {
            mLock.unlock();
        }
    }
}
