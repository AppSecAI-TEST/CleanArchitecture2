package com.cleanarchitecture.shishkin.base.repository;

import android.content.Context;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.net.Connectivity;
import com.cleanarchitecture.shishkin.base.repository.net.requests.AbstractRequest;
import com.cleanarchitecture.shishkin.base.task.PhonePausableThreadPoolExecutor;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

public class NetProvider implements INetProvider {
    public static final String NAME = "NetProvider";

    private static volatile NetProvider sInstance;
    private Picasso mPicasso;
    private boolean mConnected = false;
    private PhonePausableThreadPoolExecutor mPhonePausableThreadPoolExecutor;

    public static NetProvider getInstance() {
        if (sInstance == null) {
            synchronized (NetProvider.class) {
                if (sInstance == null) {
                    sInstance = new NetProvider();
                }
            }
        }
        return sInstance;
    }

    private NetProvider () {
        final Context context = ApplicationController.getInstance();
        if (context == null) {
            return;
        }

        mConnected = Connectivity.isNetworkConnected(context);
        mPicasso = Picasso.with(context);
        mPhonePausableThreadPoolExecutor = new PhonePausableThreadPoolExecutor(context, 10, TimeUnit.MINUTES);
    }

    @Override
    public synchronized void request(final AbstractRequest request) {
        if (mConnected) {
            mPhonePausableThreadPoolExecutor.execute(request);
        }
    }

    @Override
    public synchronized void setPaused(boolean paused) {
        mConnected = !paused;
        mPhonePausableThreadPoolExecutor.setPaused(paused);
    }

    public Picasso getPicasso() {
        return mPicasso;
    }

}

