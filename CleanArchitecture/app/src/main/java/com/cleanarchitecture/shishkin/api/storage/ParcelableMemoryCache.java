package com.cleanarchitecture.shishkin.api.storage;

import android.os.Parcel;
import android.os.Parcelable;

import com.cleanarchitecture.shishkin.api.controller.AbstractModule;
import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


public class ParcelableMemoryCache<T extends Parcelable> extends AbstractModule implements IParcelableStorage<T> {
    public static final String NAME = ParcelableMemoryCache.class.getName();
    private static final String LOG_TAG = "ParcelableMemoryCache:";
    private static final long MAX_SIZE = 1000L;
    private static final long DURATION = 3;
    private static final TimeUnit DURATION_TIMEUNIT = TimeUnit.MINUTES;
    private static final String LIST = "LIST";
    private static final String PARCELABLE = "PARCELABLE";

    private static volatile ParcelableMemoryCache sInstance;
    private LoadingCache<String, byte[]> mCache;
    private byte[] mValue;
    private ReentrantLock mLock;

    public static ParcelableMemoryCache getInstance() {
        if (sInstance == null) {
            synchronized (ParcelableMemoryCache.class) {
                if (sInstance == null) {
                    sInstance = new ParcelableMemoryCache();
                }
            }
        }
        return sInstance;
    }

    private ParcelableMemoryCache() {
        mLock = new ReentrantLock();

        mCache = CacheBuilder.newBuilder()
                .maximumSize(MAX_SIZE)
                .expireAfterWrite(DURATION, DURATION_TIMEUNIT)
                .build(
                        new CacheLoader<String, byte[]>() {
                            public byte[] load(String key) {
                                return mValue;
                            }
                        });
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void put(final String key, final T value) {
        if (StringUtils.isNullOrEmpty(key) || value == null) {
            return;
        }

        if (!validate()) {
            return;
        }

        mLock.lock();

        final Parcel parcel = Parcel.obtain();
        parcel.writeString(PARCELABLE);
        try {
            parcel.writeParcelable(value, 0);
            mValue = parcel.marshall();
            mCache.put(key, mValue);
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            parcel.recycle();
            mLock.unlock();
        }
    }

    @Override
    public void put(final String key, final List<T> values) {
        if (StringUtils.isNullOrEmpty(key) || values == null) {
            return;
        }

        if (!validate()) {
            return;
        }

        mLock.lock();

        final Parcel parcel = Parcel.obtain();
        parcel.writeString(LIST);
        try {
            parcel.writeList(values);
            mValue = parcel.marshall();
            mCache.put(key, mValue);
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            parcel.recycle();
            mLock.unlock();
        }
    }

    @Override
    public T get(final String key, final Class itemClass) {
        if (StringUtils.isNullOrEmpty(key)) {
            return null;
        }

        mLock.lock();

        final Parcel parcel = Parcel.obtain();
        try {
            final byte[] value = mCache.getIfPresent(key);
            if (value != null) {
                parcel.unmarshall(value, 0, value.length);
                parcel.setDataPosition(0);
                final String type = parcel.readString();
                if (PARCELABLE.equals(type)) {
                    return (T) parcel.readParcelable(itemClass.getClassLoader());
                }
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            parcel.recycle();
            mLock.unlock();
        }
        return null;
    }

    @Override
    public List<T> getList(final String key, final Class itemClass) {
        if (StringUtils.isNullOrEmpty(key)) {
            return null;
        }

        mLock.lock();

        final Parcel parcel = Parcel.obtain();
        try {
            final byte[] value = mCache.getIfPresent(key);
            if (value != null) {
                parcel.unmarshall(value, 0, value.length);
                parcel.setDataPosition(0);
                final String type = parcel.readString();
                if (LIST.equals(type)) {
                    final ArrayList<T> res = new ArrayList<>();
                    parcel.readList(res, itemClass.getClassLoader());
                    return res;
                }
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            parcel.recycle();
            mLock.unlock();
        }
        return null;
    }

    private boolean validate() {
        final Runtime runtime = Runtime.getRuntime();
        final long procent = 100 - ((runtime.totalMemory() - runtime.freeMemory()) * 100 / runtime.maxMemory());
        if (procent < 15) {
            return false;
        }
        return true;
    }

    @Override
    public void clear(final String key) {
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

    @Override
    public String getSubscriberType() {
        return null;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

}
