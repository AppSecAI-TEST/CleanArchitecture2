package com.cleanarchitecture.shishkin.api.controller;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;

/**
 * Контроллер, протоколирующий Uncaught Exception
 */
public class CrashController implements Thread.UncaughtExceptionHandler, IModule {

    public static final String NAME = CrashController.class.getName();
    private static final String LOG_TAG = "CrashController:";
    private static Thread.UncaughtExceptionHandler mHandler = Thread.getDefaultUncaughtExceptionHandler();

    public CrashController() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        android.util.Log.e(LOG_TAG, throwable.getMessage(), throwable);
        ErrorController.getInstance().onError(LOG_TAG, throwable);
        if (mHandler != null) {
            mHandler.uncaughtException(thread, throwable);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public void onUnRegisterModule() {
    }

    @Override
    public String getDescription() {
        final Context context = ApplicationController.getInstance();
        if (context != null) {
            return context.getString(R.string.module_crash);
        }
        return "Crash controller";
    }

}

