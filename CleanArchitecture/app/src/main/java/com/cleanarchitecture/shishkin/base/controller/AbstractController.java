package com.cleanarchitecture.shishkin.base.controller;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractController<T> implements IController<T> {

    private Map<String, WeakReference<T>> mSubscribers = Collections.synchronizedMap(new HashMap<String, WeakReference<T>>());

    private WeakReference<T> mCurrentSubscriber;

    @Override
    public abstract String getName();

    @Override
    public synchronized void register(T subscriber) {
        if (subscriber != null) {
            checkNullSubscriber();

            if (subscriber instanceof ISubscriber) {
                mSubscribers.put(((ISubscriber) subscriber).getName(), new WeakReference<T>(subscriber));
            }
        }

    }

    private synchronized void checkNullSubscriber() {
        for (Map.Entry<String, WeakReference<T>> entry : mSubscribers.entrySet()) {
            if (entry.getValue().get() == null) {
                mSubscribers.remove(entry.getKey());
            }
        }
    }

    @Override
    public synchronized void unregister(final T subscriber) {
        if (subscriber != null) {
            if (mCurrentSubscriber != null && mCurrentSubscriber.get() != null) {
                if (((ISubscriber) subscriber).getName().equalsIgnoreCase(((ISubscriber) mCurrentSubscriber.get()).getName())) {
                    mCurrentSubscriber.clear();
                    mCurrentSubscriber = null;
                }
            }

            if (subscriber instanceof ISubscriber) {
                if (mSubscribers.containsKey(((ISubscriber) subscriber).getName())) {
                    mSubscribers.remove(((ISubscriber) subscriber).getName());
                }
            }

            checkNullSubscriber();
        }
    }

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
    public synchronized Map<String, WeakReference<T>> getSubscribers() {
        return mSubscribers;
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
