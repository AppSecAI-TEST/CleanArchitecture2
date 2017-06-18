package com.cleanarchitecture.shishkin.api.controller;

import java.lang.ref.WeakReference;

public abstract class AbstractController<T> extends AbstractSmallController<T> implements IController<T> {
    private static final String LOG_TAG = "AbstractController:";

    private WeakReference<T> mCurrentSubscriber;

    @Override
    public synchronized void setCurrentSubscriber(final T subscriber) {
        if (subscriber != null) {
            mCurrentSubscriber = new WeakReference<>(subscriber);
        }
    }

    @Override
    public synchronized void unregister(final T subscriber) {
        super.unregister(subscriber);

        if (subscriber != null && mCurrentSubscriber != null && mCurrentSubscriber.get() != null) {
            if (subscriber instanceof ISubscriber) {
                if (((ISubscriber) subscriber).getName().equalsIgnoreCase(((ISubscriber) mCurrentSubscriber.get()).getName())) {
                    mCurrentSubscriber.clear();
                    mCurrentSubscriber = null;
                }
            }
        }
    }

    @Override
    public synchronized T getCurrentSubscriber() {
        if (mCurrentSubscriber != null) {
            if (mCurrentSubscriber.get() != null) {
                return mCurrentSubscriber.get();
            } else {
                mCurrentSubscriber.clear();
                mCurrentSubscriber = null;
            }
        }
        return null;
    }

    @Override
    public synchronized T getSubscriber() {
        final T currentSubscriber = getCurrentSubscriber();
        if (currentSubscriber != null) {
            return currentSubscriber;
        }

        if (!getSubscribers().isEmpty()) {
            for (WeakReference<T> weakReference : getSubscribers().values()) {
                final T subscriber = weakReference.get();
                if (subscriber != null) {
                    return subscriber;
                }
            }
        }
        ErrorController.getInstance().onError(LOG_TAG, "Subscriber controller(" + getName() + ") not found", false);
        return null;
    }

    @Override
    public synchronized boolean hasSubscribers() {
        return !getSubscribers().isEmpty();
    }

}
