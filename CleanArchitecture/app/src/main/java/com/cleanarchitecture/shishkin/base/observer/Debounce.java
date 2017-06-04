package com.cleanarchitecture.shishkin.base.observer;

import android.os.Handler;

import java.lang.ref.WeakReference;

public class Debounce implements Runnable {

    private long mDelay = 5000; //5 sec
    private int mSkip = 0;
    private Handler mHandler = null;
    private WeakReference<Object> mObject;

    public Debounce(final long delay) {
        this(delay, 0);
    }

    public Debounce(final long delay, final int skip) {
        mHandler = new Handler();
        mDelay = delay;
        mSkip = skip;
    }

    public void onEvent(final Object object) {
        if (object != null) {
            mObject = new WeakReference<>(object);
        } else {
            mObject = null;
        }

        if (mSkip >= 0) {
            mSkip--;
        }

        if (mSkip < 0) {
            mHandler.removeCallbacks(this);
            mHandler.postDelayed(this, mDelay);
        }
    }

    @Override
    public void run() {
    }

    public void finish() {
        mObject = null;

        mHandler.removeCallbacks(this);
        mHandler = null;
    }

    public Object getObject() {
        if (mObject != null) {
            return mObject.get();
        }
        return null;
    }

}
