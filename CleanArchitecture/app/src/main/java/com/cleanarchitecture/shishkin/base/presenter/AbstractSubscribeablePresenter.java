package com.cleanarchitecture.shishkin.base.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.controller.ISubscribeable;

public abstract class AbstractSubscribeablePresenter<M> extends AbstractPresenter<M> {
    // Content change observer
    private ISubscribeable mObserver;

    @Override
    public void onViewCreatedLifecycle() {
        super.onViewCreatedLifecycle();

        if (mObserver == null && ApplicationController.getInstance() != null) {
            mObserver = createContentObserver();
            mObserver.subscribe(ApplicationController.getInstance());
        }
    }

    @Override
    public void onDestroyLifecycle() {
        super.onDestroyLifecycle();

        if (mObserver != null && ApplicationController.getInstance() != null) {
            mObserver.unsubscribe(ApplicationController.getInstance());
            mObserver = null;
        }
    }

    @NonNull
    private ISubscribeable createContentObserver() {
        ISubscribeable observer = onCreateContentObserver();
        if (observer == null) {
            observer = new AbstractSubscribeablePresenter.DummyObserver();
        }
        return observer;
    }

    public abstract void onContentChanged();

    /**
     * Implement this method to provide {@link ISubscribeable} that will
     * listen to the data changes.
     *
     * @return implementation of data change observer.
     */
    @Nullable
    protected abstract ISubscribeable onCreateContentObserver();

    private static final class DummyObserver implements ISubscribeable {

        public void subscribe(final Context context) {
        }

        public void unsubscribe(final Context context) {
        }

    }

}
