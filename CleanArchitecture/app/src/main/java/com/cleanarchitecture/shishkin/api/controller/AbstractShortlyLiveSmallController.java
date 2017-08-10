package com.cleanarchitecture.shishkin.api.controller;

import com.cleanarchitecture.shishkin.api.handler.AutoCompleteHandler;

import java.util.concurrent.TimeUnit;

public abstract class AbstractShortlyLiveSmallController<T extends ISubscriber> extends AbstractSmallController<T> implements ISmallController<T>, AutoCompleteHandler.OnShutdownListener {

    private AutoCompleteHandler<Boolean> mServiceHandler;
    private static final TimeUnit TIMEUNIT = TimeUnit.SECONDS;
    private static final long TIMEUNIT_DURATION = 10L;

    public AbstractShortlyLiveSmallController() {
        mServiceHandler = new AutoCompleteHandler<>("AbstractShortlyLiveSmallController [" + getName() + "]");
        ;
        mServiceHandler.setOnShutdownListener(this);
        setShutdownTimeout(TIMEUNIT.toMillis(TIMEUNIT_DURATION));
    }

    public void setShutdownTimeout(final long shutdownTimeout) {
        if (shutdownTimeout > 0) {
            mServiceHandler.setShutdownTimeout(shutdownTimeout);
        }
    }

    @Override
    public synchronized void onUnRegisterLastSubscriber() {
        mServiceHandler.post(true);
    }

    @Override
    public synchronized void onShutdown(AutoCompleteHandler handler) {
        if (mSubscribers.isEmpty()) {
            Admin.getInstance().unregisterModule(getName());
        }
    }
}
