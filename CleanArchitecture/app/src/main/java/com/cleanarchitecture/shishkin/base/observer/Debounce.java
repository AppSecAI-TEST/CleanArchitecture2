package com.cleanarchitecture.shishkin.base.observer;

import android.os.Handler;

public class Debounce implements Runnable {

    private long mDelay = 5000; //5 sec
    private int mSkip = 0;
    private Handler mHandler = null;

    public Debounce(final long delay) {
        this(delay, 0);
    }

    public Debounce(final long delay, final int skip) {
        mHandler = new Handler();
        mDelay = delay;
        mSkip = skip;
    }

    public void onEvent() {
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
        mHandler.removeCallbacks(this);
        mHandler = null;
    }
}
