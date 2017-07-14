package com.cleanarchitecture.shishkin.api.observer;

import android.os.Handler;

import java.util.Observable;

public abstract class AbstractDebouncedObserver extends AbstractObserver implements Runnable {

    private long mDelay = 1000;
    private Handler mHandler = null;
    private Object mArg = null;
    private int mSkip = 0;


    public AbstractDebouncedObserver(final Observable observable, final long delay) {
        super(observable);

        mHandler = new Handler();
        mDelay = delay;
    }

    public AbstractDebouncedObserver(final Observable observable, final long delay, final int skip) {
        this(observable, delay);

        mSkip = skip;
    }

    @Override
    public synchronized void update(final Observable o, final Object arg) {
        if (mSkip >= 0) {
            mSkip--;
        }

        if (mSkip < 0) {
            mArg = arg;
            mHandler.removeCallbacks(this);
            mHandler.postDelayed(this, mDelay);
        }
    }

    @Override
    public void run() {
        onRun(mArg);
    }

    @Override
    public void finish() {
        mHandler.removeCallbacks(this);
        mHandler = null;

        super.finish();
    }

    public abstract void onRun(Object arg);
}
