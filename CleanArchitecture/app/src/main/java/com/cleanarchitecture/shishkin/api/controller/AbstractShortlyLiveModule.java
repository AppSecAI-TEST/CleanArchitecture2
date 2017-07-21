package com.cleanarchitecture.shishkin.api.controller;

import com.cleanarchitecture.shishkin.api.service.AutoCompleteHandler;

import java.util.concurrent.TimeUnit;

public abstract class AbstractShortlyLiveModule extends AbstractModule implements AutoCompleteHandler.OnShutdownListener {

    private AutoCompleteHandler<Boolean> mServiceHandler;
    private static final TimeUnit TIMEUNIT = TimeUnit.SECONDS;
    private static final long TIMEUNIT_DURATION = 30L;

    public AbstractShortlyLiveModule() {
        mServiceHandler = new AutoCompleteHandler<>("AbstractShortlyLiveModule [" + getName() + "]");
        mServiceHandler.setOnShutdownListener(this);
        mServiceHandler.post(true);
        setShutdownTimeout(TIMEUNIT.toMillis(TIMEUNIT_DURATION));

    }

    public void setShutdownTimeout(final long shutdownTimeout) {
        if (shutdownTimeout > 0) {
            mServiceHandler.setShutdownTimeout(shutdownTimeout);
        }
    }

    public void post() {
        mServiceHandler.post(true);
    }

    @Override
    public void onShutdown(AutoCompleteHandler handler) {
        Admin.getInstance().unregister(getName());
    }
}
