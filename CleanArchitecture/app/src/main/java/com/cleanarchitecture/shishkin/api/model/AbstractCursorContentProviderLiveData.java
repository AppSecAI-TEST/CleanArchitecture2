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
    private boolean isChanged = false;

    private ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            onChanged();

            if (AbstractCursorContentProviderLiveData.this.hasObservers()) {
                getData();
            } else {
                isChanged = true;
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
     * Событие - данные изменены в Content Provider
     */
    public void onChanged() {
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
        removeCursor();
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

    /**
     * Удалить курсор используемый для выборки данных
     */
    public abstract void removeCursor();

}
