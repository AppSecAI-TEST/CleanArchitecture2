package com.cleanarchitecture.shishkin.base.controller;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

public abstract class BackgroundIntentService extends Service{

    private String mName;
    private HandlerThread mWorkerThread;
    private BackgroundHandler mHandler;

    private final class BackgroundHandler extends Handler {
        public BackgroundHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            onHandleIntent((Intent)msg.obj);
        }
    }

    /**
     * Creates the BackgroundService
     *
     * @param name Used to name the worker thread
     */
    public BackgroundIntentService(String name) {
        super();
        mName = name;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWorkerThread = new HandlerThread(mName, Process.THREAD_PRIORITY_FOREGROUND);
        mWorkerThread.setPriority(Thread.MAX_PRIORITY);
        mWorkerThread.start();
        mHandler = new BackgroundHandler(mWorkerThread.getLooper());
    }

    /**
     * You should not override this method for your BackgroundService. Instead,
     * override {@link #onHandleIntent(Intent)}, which the system calls when the BackgroundService
     * receives a start request.
     * @see android.app.Service#onStartCommand
     */
    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Message msg = mHandler.obtainMessage();
        msg.obj = intent;
        mHandler.sendMessage(msg);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mWorkerThread.quit();
    }

    /**
     * This is the method that will receive intent messages from
     * {@link android.content.Context#startService(Intent)}.
     * Logic is similar to the one implemented by {@link android.app.IntentService}
     * @see android.app.IntentService#onHandleIntent(Intent)
     */
    @WorkerThread
    public abstract void onHandleIntent(@Nullable Intent intent);
}
