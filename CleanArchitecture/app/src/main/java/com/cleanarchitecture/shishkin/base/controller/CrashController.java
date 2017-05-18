package com.cleanarchitecture.shishkin.base.controller;

import com.github.snowdream.android.util.Log;

/**
 * Контроллер, протоколирующий Uncaught Exception
 */
public class CrashController implements Thread.UncaughtExceptionHandler {

    private static final String LOG_TAG = "CrashController";
    private static volatile CrashController sInstance;
    private static Thread.UncaughtExceptionHandler mHandler;

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
        mHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Log.e(LOG_TAG, throwable);
        if (mHandler != null) {
            mHandler.uncaughtException(thread, throwable);
        }
    }
}

