package com.cleanarchitecture.shishkin.api.controller;

import com.cleanarchitecture.shishkin.common.state.IStateable;
import com.cleanarchitecture.shishkin.common.state.ViewStateObserver;

import java.lang.ref.WeakReference;
import java.util.Map;

public abstract class AbstractShortlyLiveController<T extends ISubscriber> extends AbstractShortlyLiveSmallController<T> implements IController<T> {

    private WeakReference<T> mCurrentSubscriber;

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
    public synchronized T getSubscriber() {
        final T subscriber = getCurrentSubscriber();
        if (subscriber != null) {
            return subscriber;
        }

        if (!mSubscribers.isEmpty()) {
            return mSubscribers.entrySet().iterator().next().getValue().get();
        } else {
            ErrorController.getInstance().onError(getName(), "Subscribers not found", false);
        }
        return null;
    }

    @Override
    public synchronized T getSubscriber(final String name) {
        checkNullSubscriber();

        if (getSubscribers().containsKey(name)) {
            for (Map.Entry<String, WeakReference<T>> entry : getSubscribers().entrySet()) {
                if (entry.getValue().get().getName().equalsIgnoreCase(name)) {
                    return entry.getValue().get();
                }
            }
        }
        return null;
    }

    @Override
    public synchronized void setCurrentSubscriber(final T subscriber) {
        if (subscriber != null) {
            mCurrentSubscriber = new WeakReference<>(subscriber);
        }
    }

    @Override
    public synchronized T getCurrentSubscriber() {
        if (mCurrentSubscriber != null && mCurrentSubscriber.get() != null) {
            return mCurrentSubscriber.get();
        }

        checkNullSubscriber();

        if (!mSubscribers.isEmpty()) {
            for (WeakReference<T> ref : mSubscribers.values()) {
                if (ref.get() instanceof IStateable) {
                    if (((IStateable) ref.get()).getState() == ViewStateObserver.STATE_RESUME) {
                        return ref.get();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public synchronized Map<String, WeakReference<T>> getSubscribers() {
        checkNullSubscriber();

        return mSubscribers;
    }

    @Override
    public synchronized boolean hasSubscribers() {
        checkNullSubscriber();

        return (!mSubscribers.isEmpty());
    }

}
