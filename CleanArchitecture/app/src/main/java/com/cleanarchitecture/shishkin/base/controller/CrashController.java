package com.cleanarchitecture.shishkin.base.controller;

import com.github.snowdream.android.util.Log;

/**
 * Контроллер, протоколирующий Uncaught Exception
 */
public class CrashController implements Thread.UncaughtExceptionHandler {

    private static final String LOG_TAG = "CrashController";
    private static volatile CrashController sInstance;
    private static Thread.UncaughtExceptionHandler mHandler = Thread.getDefaultUncaughtExceptionHandler();

    public static void instantiate() {
        if (sInstance == null) {
            synchronized (CrashController.class) {
                if (sInstance == null) {
                    sInstance = new CrashController();
                }
            }
        }
    }

    private CrashController() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        android.util.Log.e(LOG_TAG, throwable.getMessage(), throwable);
        Log.e(LOG_TAG, throwable);
        if (mHandler != null) {
            mHandler.uncaughtException(thread, throwable);
        }
    }
}

