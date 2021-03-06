package com.cleanarchitecture.shishkin.api.controller;

import com.cleanarchitecture.shishkin.api.ui.activity.AbstractActivity;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSmallController<T extends ISubscriber> implements ISmallController<T> {

    protected Map<String, WeakReference<T>> mSubscribers = Collections.synchronizedMap(new ConcurrentHashMap<String, WeakReference<T>>());

    protected synchronized void checkNullSubscriber() {
        for (Map.Entry<String, WeakReference<T>> entry : mSubscribers.entrySet()) {
            if (entry.getValue() == null || entry.getValue().get() == null) {
                mSubscribers.remove(entry.getKey());
            }
        }
    }

    @Override
    public synchronized void register(T subscriber) {
        checkNullSubscriber();

        if (subscriber == null) {
            return;
        }

        final int cnt = mSubscribers.size();
        mSubscribers.put(subscriber.getName(), new WeakReference<>(subscriber));
        if (cnt == 0 && mSubscribers.size() == 1) {
            onRegisterFirstSubscriber();
        }
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

        if (mSubscribers.isEmpty()) {
            onUnRegisterLastSubscriber();
        }
    }

    /**
     * Событие - появился первый подписчик
     */
    public synchronized void onRegisterFirstSubscriber() {
    }

    /**
     * Событие - отписан последний подписчик
     */
    public synchronized void onUnRegisterLastSubscriber() {
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public void onUnRegisterModule() {
    }

}
