package com.cleanarchitecture.shishkin.base.observer;

import android.os.Handler;

public class Debounce implements Runnable {

    private long mDelay = 5000; //5 sec
    private int mLost = 0;
    private Handler mHandler = null;

    public Debounce(final long delay) {
        this(delay, 0);
    }

    public Debounce(final long delay, final int lost) {
        mHandler = new Handler();
        mDelay = delay;
        mLost = lost;
    }

    public void onEvent() {
        if (mLost >= 0) {
            mLost--;
        }

        if (mLost < 0) {
            mHandler.removeCallbacks(this);
            mHandler.postDelayed(this, mDelay);
        }
    }

    @Override
    public void run() {
    }

    public void finish() {
        mHandler.removeCallbacks(this);
        mHandler.getLooper().quit();
        mHandler = null;
    }
}
