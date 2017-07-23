package com.cleanarchitecture.shishkin.api.model;

import android.arch.lifecycle.LiveData;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.EventBusController;
import com.cleanarchitecture.shishkin.api.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.api.debounce.LivingDataDebounce;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractContentProviderLiveData<T> extends LiveData<T> implements IModuleSubscriber {

    private List<Uri> mUris = new ArrayList<>();
    private boolean isChanged = false;
    private LivingDataDebounce mDebounce = null;
    private ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            onChanged();

            if (AbstractContentProviderLiveData.this.hasObservers()) {
                if (mDebounce == null || getValue() == null) {
                    getData();
                } else {
                    mDebounce.onEvent();
                }
            } else {
                isChanged = true;
            }
        }
    };

    public AbstractContentProviderLiveData(final Uri uri) {
        super();

        mUris.add(uri);
    }

    public AbstractContentProviderLiveData(final List<Uri> uris) {
        super();

        mUris.addAll(uris);
    }

    /**
     * Событие - данные изменены в Content Provider
     */
    public void onChanged() {
    }

    /**
     * Установить задержку для повторной выборки данных
     *
     * @param delay the delay
     */
    public void setDebounce(final long delay) {
        mDebounce = new LivingDataDebounce(this, delay);
    }

    @Override
    protected void onActive() {
        isChanged = false;
        AdminUtils.register(this);
        final Context context = AdminUtils.getContext();
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
        isChanged = false;
        AdminUtils.unregister(this);
        final Context context = AdminUtils.getContext();
        if (context != null) {
            context.getContentResolver().unregisterContentObserver(mContentObserver);
        }

        if (mDebounce != null) {
            mDebounce.finish();
        }
    }

    @Override
    public List<String> hasSubscriberType() {
        final ArrayList<String> list = new ArrayList<>();
        list.add(EventBusController.SUBSCRIBER_TYPE);
        return list;
    }

    @Override
    public T getValue() {
        if (isChanged) {
            isChanged = false;
            getData();
        }
        return super.getValue();
    }

    /**
     * Получить данные
     */
    public abstract void getData();

}
