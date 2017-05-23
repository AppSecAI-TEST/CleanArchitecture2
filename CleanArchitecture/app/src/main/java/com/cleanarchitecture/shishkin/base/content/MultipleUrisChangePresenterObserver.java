package com.cleanarchitecture.shishkin.base.content;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObservable;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.cleanarchitecture.shishkin.base.controller.ISubscribeable;
import com.cleanarchitecture.shishkin.base.presenter.AbstractSubscribeablePresenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Receives call backs for changes to content.
 * Must be implemented by objects which are added to a {@link ContentObservable}.
 */
public class MultipleUrisChangePresenterObserver extends ContentObserver implements ISubscribeable {

    private WeakReference<AbstractSubscribeablePresenter<?>> mPresenter;
    private List<Uri> mUris = new ArrayList<>();

    public MultipleUrisChangePresenterObserver(final AbstractSubscribeablePresenter<?> presenter, List<Uri> uris) {
        super(new Handler());

        if (presenter != null) {
            mPresenter = new WeakReference<>(presenter);
        }

        if (uris != null && !uris.isEmpty()) {
            mUris.addAll(uris);
        }
    }

    /**
     * Adds content {@link Uri} to be notified on data change.
     *
     * @param contentUri The URI to watch for changes. This can be a specific row URI, or a base URI
     * for a whole class of content.
     */
    public void add(final Uri contentUri) {
        if (contentUri != null) {
            mUris.add(contentUri);
        }
    }

    @Override
    public void subscribe(final Context context) {
        if (context != null) {
            final ContentResolver contentResolver = context.getContentResolver();
            for (final Uri uri : mUris) {
                contentResolver.registerContentObserver(uri, true, this);
            }
        }
    }

    /**
     * This method is called when a content change occurs.
     */
    @Override
    public void onChange(final boolean selfChange) {
        super.onChange(selfChange);

        if (mPresenter != null && mPresenter.get() != null) {
            mPresenter.get().onContentChanged();
        }
    }

    @Override
    public void unsubscribe(final Context context) {
        if (context != null) {
            context.getContentResolver().unregisterContentObserver(this);
        }
    }

}
