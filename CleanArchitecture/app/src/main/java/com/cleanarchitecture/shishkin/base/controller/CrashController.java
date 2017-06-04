package com.cleanarchitecture.shishkin.base.controller;

import com.github.snowdream.android.util.Log;

/**
 * Контроллер, протоколирующий Uncaught Exception
 */
public class CrashController implements Thread.UncaughtExceptionHandler, ISubscriber {

    public static final String NAME = "CrashController";
    private static Thread.UncaughtExceptionHandler mHandler = Thread.getDefaultUncaughtExceptionHandler();

    public CrashController() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        android.util.Log.e(NAME, throwable.getMessage(), throwable);
        ErrorController.getInstance().onError(NAME, throwable);
        if (mHandler != null) {
            mHandler.uncaughtException(thread, throwable);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}

