package com.cleanarchitecture.shishkin.api.controller;

import com.github.snowdream.android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSmallController<T> implements ISmallController<T> {

    private static final String LOG_TAG = "AbstractSmallController:";

    private Map<String, WeakReference<T>> mSubscribers = Collections.synchronizedMap(new ConcurrentHashMap<String, WeakReference<T>>());

    @Override
    public abstract String getName();

    @Override
    public synchronized void register(T subscriber) {
        if (subscriber == null) {
            return;
        }

        checkNullSubscriber();

        if (subscriber instanceof ISubscriber) {
            //Log.i(LOG_TAG, ((ISubscriber) subscriber).getName() + " зарегистрирован в " + getName());
            mSubscribers.put(((ISubscriber) subscriber).getName(), new WeakReference<T>(subscriber));
        }
    }

    @Override
    public synchronized void unregister(final T subscriber) {
        checkNullSubscriber();
    }

    private synchronized void checkNullSubscriber() {
        for (Map.Entry<String, WeakReference<T>> entry : mSubscribers.entrySet()) {
            if (entry.getValue() == null || entry.getValue().get() == null) {
                Log.i(LOG_TAG, entry.getKey() + " отключен в " + getName());
                mSubscribers.remove(entry.getKey());
            }
        }
    }

    @Override
    public synchronized Map<String, WeakReference<T>> getSubscribers() {
        checkNullSubscriber();

        return mSubscribers;
    }

    @Override
    public abstract String getSubscriberType();

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public void onUnRegister() {
    }

    @Override
    public boolean isRegistered(final T subscriber) {
        if (subscriber == null) {
            return false;
        }

        checkNullSubscriber();

        return (mSubscribers.containsKey(((ISubscriber) subscriber).getName()));
    }
}
