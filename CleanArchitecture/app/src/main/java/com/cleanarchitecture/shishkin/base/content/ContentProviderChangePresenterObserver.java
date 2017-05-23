package com.cleanarchitecture.shishkin.base.content;

import android.content.Context;
import android.database.ContentObservable;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.cleanarchitecture.shishkin.base.controller.ISubscribeable;
import com.cleanarchitecture.shishkin.base.presenter.AbstractSubscribeablePresenter;

import java.lang.ref.WeakReference;

/**
 * Receives call backs for changes to content.
 * Must be implemented by objects which are added to a {@link ContentObservable}.
 */
public class ContentProviderChangePresenterObserver extends ContentObserver
        implements ISubscribeable {

    private WeakReference<AbstractSubscribeablePresenter<?>> mPresenter;
    private Uri mUri;

    public ContentProviderChangePresenterObserver(final AbstractSubscribeablePresenter<?> presenter, final Uri uri) {
        super(new Handler());

        if (presenter != null) {
            mPresenter = new WeakReference<>(presenter);
        }
        if (uri != null) {
            mUri = uri;
        }
    }

    @Override
    public void subscribe(final Context context) {
        if (context != null && mUri != null) {
            context.getContentResolver().registerContentObserver(mUri, true, this);
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
