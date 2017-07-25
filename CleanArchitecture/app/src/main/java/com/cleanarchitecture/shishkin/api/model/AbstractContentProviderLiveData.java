package com.cleanarchitecture.shishkin.api.model;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.cleanarchitecture.shishkin.api.controller.AdminUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractContentProviderLiveData<T> extends LiveData<T> implements ILiveData<T> {

    private List<Uri> mUris = new ArrayList<>();
    private boolean mChanged = false;
    private IDatastore mDatastore;

    private ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            if (AbstractContentProviderLiveData.this.hasObservers() && mDatastore != null) {
                mDatastore.onChangeData();
            } else {
                mChanged = true;
            }
        }
    };

    public AbstractContentProviderLiveData(final Uri uri) {
        super();

        mDatastore = getDatastore();

        mUris.add(uri);
    }

    public AbstractContentProviderLiveData(final List<Uri> uris) {
        super();

        mDatastore = getDatastore();

        mUris.addAll(uris);
    }

    public abstract IDatastore getDatastore();

    @Override
    public void observe(LifecycleOwner owner, Observer<T> observer) {
        final boolean hasObservers = hasObservers();

        super.observe(owner, observer);

        if (!hasObservers && mChanged) {
            getData();
        }
    }

    @Override
    protected void onActive() {
        if (mDatastore != null) {
            AdminUtils.register(mDatastore);
        }

        final Context context = AdminUtils.getContext();
        if (context != null) {
            final ContentResolver contentResolver = context.getContentResolver();
            for (final Uri uri : mUris) {
                contentResolver.registerContentObserver(uri, false, mContentObserver);
            }
            if (getValue() == null) {
                getData();
            }
        }
    }

    @Override
    protected void onInactive() {
        if (mDatastore != null) {
            AdminUtils.unregister(mDatastore);
        }

        final Context context = AdminUtils.getContext();
        if (context != null) {
            context.getContentResolver().unregisterContentObserver(mContentObserver);
        }
    }

    /**
     * Получить данные
     */
    public void getData() {
        mChanged = false;

        if (mDatastore != null) {
            mDatastore.getData();
        }
    }

    /**
     * Прервать выборку данных
     */
    public void terminate() {
        if (mDatastore != null) {
            mDatastore.terminate();
        }
    }

    public void setValue(T object) {
        super.setValue(object);
    }

    public void postValue(T object) {
        super.postValue(object);
    }

}
