package com.cleanarchitecture.shishkin.api.controller;

import java.lang.ref.WeakReference;

public abstract class AbstractController<T extends ISubscriber> extends AbstractSmallController<T> implements IController<T> {

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
            if (subscriber.getName().equalsIgnoreCase(mCurrentSubscriber.get().getName())) {
                mCurrentSubscriber.clear();
                mCurrentSubscriber = null;
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
        final T subscriber = getCurrentSubscriber();
        if (subscriber != null) {
            return subscriber;
        }

        return super.getSubscriber();
    }

}
