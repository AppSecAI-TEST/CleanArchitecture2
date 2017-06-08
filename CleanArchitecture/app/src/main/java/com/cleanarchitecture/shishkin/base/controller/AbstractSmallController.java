package com.cleanarchitecture.shishkin.base.controller;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractSmallController<T> implements ISmallController<T> {

    protected Map<String, WeakReference<T>> mSubscribers = Collections.synchronizedMap(new HashMap<String, WeakReference<T>>());

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

    public synchronized Map<String, WeakReference<T>> getSubscribers() {
        return mSubscribers;
    }

    @Override
    public synchronized void unregister(final T subscriber) {
        if (subscriber != null) {
            if (subscriber instanceof ISubscriber) {
                if (mSubscribers.containsKey(((ISubscriber) subscriber).getName())) {
                    mSubscribers.remove(((ISubscriber) subscriber).getName());
                }
            }

            checkNullSubscriber();
        }
    }

    @Override
    public abstract String getSubscriberType();


}
