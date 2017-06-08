package com.cleanarchitecture.shishkin.base.controller;

import java.lang.ref.WeakReference;

public abstract class AbstractController<T> extends AbstractSmallController<T> implements IController<T> {

    private WeakReference<T> mCurrentSubscriber;

    @Override
    public synchronized void setCurrentSubscriber(final T subscriber) {
        if (subscriber != null) {
            mCurrentSubscriber = new WeakReference<>(subscriber);
        }
    }

    @Override
    public synchronized T getCurrentSubscriber() {
        if (mCurrentSubscriber != null) {
            return mCurrentSubscriber.get();
        }
        return null;
    }

    @Override
    public synchronized T getSubscriber() {
        final T currentSubscriber = getCurrentSubscriber();
        if (currentSubscriber != null) {
            return currentSubscriber;
        }

        for (WeakReference<T> weakReference : getSubscribers().values()) {
            final T subscriber = weakReference.get();
            if (subscriber != null) {
                return subscriber;
            }
        }
        return null;
    }

}
