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

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCursorContentProviderLiveData<T> extends LiveData<T> implements IModuleSubscriber {

    private List<Uri> mUris = new ArrayList<>();

    private ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            terminate();

            clearValue();

            if (AbstractCursorContentProviderLiveData.this.hasObservers()) {
                getData();
            }
        }
    };

    public AbstractCursorContentProviderLiveData(final Uri uri) {
        super();

        mUris.add(uri);
    }

    public AbstractCursorContentProviderLiveData(final List<Uri> uris) {
        super();

        mUris.addAll(uris);
    }

    /**
     * Очистить данные
     */
    public abstract void clearValue();

    @Override
    protected void onActive() {
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
        AdminUtils.unregister(this);
        final Context context = AdminUtils.getContext();
        if (context != null) {
            context.getContentResolver().unregisterContentObserver(mContentObserver);
        }
    }

    @Override
    public List<String> hasSubscriberType() {
        final ArrayList<String> list = new ArrayList<>();
        list.add(EventBusController.SUBSCRIBER_TYPE);
        return list;
    }

    /**
     * Получить данные
     */
    public abstract void getData();

    /**
     * Прервать выборку данных
     */
    public abstract void terminate();

}
