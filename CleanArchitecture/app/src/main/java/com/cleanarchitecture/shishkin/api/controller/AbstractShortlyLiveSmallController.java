package com.cleanarchitecture.shishkin.api.controller;

import com.cleanarchitecture.shishkin.api.handler.AutoCompleteHandler;
import com.cleanarchitecture.shishkin.api.ui.activity.AbstractActivity;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public abstract class AbstractShortlyLiveSmallController<T extends ISubscriber> implements ISmallController<T>,AutoCompleteHandler.OnShutdownListener {

    protected Map<String, WeakReference<T>> mSubscribers = Collections.synchronizedMap(new ConcurrentHashMap<String, WeakReference<T>>());
    private AutoCompleteHandler<Boolean> mServiceHandler;
    private static final TimeUnit TIMEUNIT = TimeUnit.SECONDS;
    private static final long TIMEUNIT_DURATION = 30L;

    public AbstractShortlyLiveSmallController() {
        mServiceHandler = new AutoCompleteHandler<>("AbstractShortlyLiveSmallController [" + getName() + "]");;
        mServiceHandler.setOnShutdownListener(this);
        setShutdownTimeout(TIMEUNIT.toMillis(TIMEUNIT_DURATION));
    }

    public void setShutdownTimeout(final long shutdownTimeout) {
        if (shutdownTimeout > 0) {
            mServiceHandler.setShutdownTimeout(shutdownTimeout);
        }
    }

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
                if (mSubscribers.isEmpty()) {
                    mServiceHandler.post(true);
                }
            }
        }
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public void onUnRegister() {
    }

    @Override
    public synchronized void onShutdown(AutoCompleteHandler handler) {
        if (mSubscribers.isEmpty()) {
            Admin.getInstance().unregisterModule(getName());
        }
    }
}
