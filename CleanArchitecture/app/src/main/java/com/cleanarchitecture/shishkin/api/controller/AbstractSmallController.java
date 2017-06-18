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
            if (mSubscribers.containsKey(((ISubscriber) subscriber).getName())) {
                mSubscribers.remove(((ISubscriber) subscriber).getName());
            }
            //Log.i(LOG_TAG, ((ISubscriber) subscriber).getName() + " зарегистрирован в " + getName());
            mSubscribers.put(((ISubscriber) subscriber).getName(), new WeakReference<T>(subscriber));
        }
    }

    private synchronized void checkNullSubscriber() {
        for (Map.Entry<String, WeakReference<T>> entry : mSubscribers.entrySet()) {
            if (entry.getValue() == null || entry.getValue().get() == null) {
                mSubscribers.remove(entry.getKey());
            }
        }
    }

    public synchronized Map<String, WeakReference<T>> getSubscribers() {
        return mSubscribers;
    }

    @Override
    public synchronized void unregister(final T subscriber) {
        if (subscriber == null) {
            return;
        }

        checkNullSubscriber();

        if (subscriber instanceof ISubscriber) {
            if (mSubscribers.containsKey(((ISubscriber) subscriber).getName())) {
                final WeakReference<T> reference = mSubscribers.get(((ISubscriber) subscriber).getName());
                if (reference.get() == subscriber) {
                    //Log.i(LOG_TAG, ((ISubscriber) subscriber).getName() + " исключен в " + getName());
                    mSubscribers.remove(((ISubscriber) subscriber).getName());
                }
            }
        }
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

}
