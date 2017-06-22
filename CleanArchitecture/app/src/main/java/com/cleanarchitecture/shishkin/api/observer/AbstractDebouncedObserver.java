package com.cleanarchitecture.shishkin.api.observer;

import android.os.Handler;

import java.util.Observable;

public abstract class AbstractDebouncedObserver extends AbstractObserver implements Runnable {

    private long mDelay = 1000;
    private Handler mHandler = null;
    private Object mArg = null;

    public AbstractDebouncedObserver(final Observable observable, final long delay) {
        super(observable);

        mHandler = new Handler();
        mDelay = delay;
    }

    @Override
    public synchronized void update(final Observable o, final Object arg) {
        mArg = arg;
        mHandler.removeCallbacks(this);
        mHandler.postDelayed(this, mDelay);
    }

    @Override
    public void run() {
        onRun(mArg);
    }

    @Override
    public void finish() {
        mHandler.removeCallbacks(this);
        super.finish();
    }

    public abstract void onRun(Object arg);
}
