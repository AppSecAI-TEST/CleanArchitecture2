package com.cleanarchitecture.shishkin.api.controller;

/**
 * Контроллер, протоколирующий Uncaught Exception
 */
public class CrashController implements Thread.UncaughtExceptionHandler, IModule {

    public static final String NAME = CrashController.class.getName();
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

    @Override
    public String getSubscriberType() {
        return null;
    }

}

