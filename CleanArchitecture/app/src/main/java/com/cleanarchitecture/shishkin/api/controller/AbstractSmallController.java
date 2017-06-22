package com.cleanarchitecture.shishkin.api.controller;

import com.cleanarchitecture.shishkin.api.presenter.IPresenter;
import com.cleanarchitecture.shishkin.api.ui.activity.AbstractActivity;
import com.cleanarchitecture.shishkin.api.ui.activity.IActivity;
import com.cleanarchitecture.shishkin.common.lifecycle.IStateable;
import com.cleanarchitecture.shishkin.common.lifecycle.Lifecycle;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSmallController<T extends ISubscriber> implements ISmallController<T> {

    private Map<String, WeakReference<T>> mSubscribers = Collections.synchronizedMap(new ConcurrentHashMap<String, WeakReference<T>>());

    @Override
    public synchronized void register(T subscriber) {
        checkNullSubscriber();

        if (subscriber == null) {
            return;
        }

        mSubscribers.put(subscriber.getName(), new WeakReference<>(subscriber));
    }

    @Override
    public synchronized void unregister(final T subscriber) {
        checkNullSubscriber();

        if (subscriber == null) {
            return;
        }

        if (!(subscriber instanceof AbstractActivity)) {
            if (mSubscribers.containsKey(subscriber.getName())) {
                mSubscribers.remove(subscriber.getName());
            }
        }
    }

    private synchronized void checkNullSubscriber() {
        for (Map.Entry<String, WeakReference<T>> entry : mSubscribers.entrySet()) {
            if (entry.getValue() == null || entry.getValue().get() == null) {
                mSubscribers.remove(entry.getKey());
            }
        }
    }

    /**
     * Получить подписчика
     *
     * @return подписчик
     */
    @Override
    public synchronized T getSubscriber() {
        checkNullSubscriber();

        if (!mSubscribers.isEmpty()) {
            final T subscriber = mSubscribers.entrySet().iterator().next().getValue().get();
            if (subscriber instanceof IStateable) {
                for (WeakReference<T> ref: mSubscribers.values()) {
                    if (ref.get() instanceof IStateable) {
                        if (((IStateable) ref.get()).getState() == Lifecycle.STATE_RESUME) {
                            return ref.get();
                        }
                    }
                }
            }
            return subscriber;
        } else {
            ErrorController.getInstance().onError(getName(), "Subscribers not found", false);
        }
        return null;
    }

    @Override
    public synchronized T getSubscriber(final String name) {
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
    public synchronized Map<String, WeakReference<T>> getSubscribers() {
        checkNullSubscriber();

        return mSubscribers;
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public void onUnRegister() {
    }

    @Override
    public synchronized boolean hasSubscribers() {
        return (!mSubscribers.isEmpty());
    }

}
