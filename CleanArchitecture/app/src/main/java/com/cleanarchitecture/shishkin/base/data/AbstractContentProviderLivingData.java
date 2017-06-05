package com.cleanarchitecture.shishkin.base.data;

import android.arch.lifecycle.LiveData;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.controller.EventBusController;
import com.cleanarchitecture.shishkin.base.observer.LivingDataDebounce;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractContentProviderLivingData<T> extends LiveData<T> {

    private List<Uri> mUris = new ArrayList<>();
    private boolean isChanged = true;
    private LivingDataDebounce mDebounce = null;
    private ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            if (AbstractContentProviderLivingData.this.hasObservers()) {
                if (mDebounce == null || getValue() == null) {
                    getData();
                } else {
                    mDebounce.onEvent(this);
                }
            } else {
                isChanged = true;
            }
        }
    };

    public AbstractContentProviderLivingData(final Uri uri) {
        super();

        mUris.add(uri);
    }

    public AbstractContentProviderLivingData(final List<Uri> uris) {
        super();

        mUris.addAll(uris);
    }

    public void setDebounce(final long delay) {
        mDebounce = new LivingDataDebounce(this, delay);
    }

    @Override
    protected void onActive() {
        EventBusController.getInstance().register(this);

        final Context context = ApplicationController.getInstance();
        if (context != null) {
            final ContentResolver contentResolver = context.getContentResolver();
            for (final Uri uri : mUris) {
                contentResolver.registerContentObserver(uri, true, mContentObserver);
            }

            getData();
        }
    }

    @Override
    protected void onInactive() {
        EventBusController.getInstance().unregister(this);

        final Context context = ApplicationController.getInstance();
        if (context != null) {
            final ContentResolver contentResolver = context.getContentResolver();
            for (final Uri uri : mUris) {
                contentResolver.unregisterContentObserver(mContentObserver);
            }
        }

        if (mDebounce != null) {
            mDebounce.finish();
        }
    }

    @Override
    public T getValue() {
        if (isChanged) {
            isChanged = false;
            getData();
        }
        return super.getValue();
    }

    public abstract void getData();


}
