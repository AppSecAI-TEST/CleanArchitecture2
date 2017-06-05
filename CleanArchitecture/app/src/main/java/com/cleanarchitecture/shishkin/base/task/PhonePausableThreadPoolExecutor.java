package com.cleanarchitecture.shishkin.base.task;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.lifecycle.Lifecycle;
import com.cleanarchitecture.shishkin.base.net.Connectivity;
import com.cleanarchitecture.shishkin.base.repository.requests.AbstractRequest;
import com.cleanarchitecture.shishkin.base.repository.requests.IRequest;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class PhonePausableThreadPoolExecutor implements IPhonePausableThreadPoolExecutor {
    private static int QUEUE_CAPACITY = 1024;


    private int mThreadCount = 1;
    private int mMaxThreadCount = 1;
    private long mKeepAliveTime = 10; // 10 мин
    private TimeUnit mUnit = TimeUnit.MINUTES;
    private PausableThreadPoolExecutor mPausableThreadPoolExecutor;

    public PhonePausableThreadPoolExecutor(final Context context, final long keepAliveTime, final TimeUnit unit) {
        mKeepAliveTime = keepAliveTime;
        mUnit = unit;

        if (context != null) {
            setThreadCount(Connectivity.getActiveNetworkInfo(context));
        }

        final BlockingQueue queue = new PriorityBlockingQueue<AbstractRequest>(QUEUE_CAPACITY, (o1, o2) -> o2.getRank() - o1.getRank());
        mPausableThreadPoolExecutor = new PausableThreadPoolExecutor(mThreadCount, mMaxThreadCount, mKeepAliveTime, mUnit, queue);
    }

    private void setThreadCount(final NetworkInfo info) {
        if (info == null || !info.isConnectedOrConnecting()) {
            mThreadCount = 1;
            mMaxThreadCount = 1;
            return;
        }

        switch (info.getType()) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_WIMAX:
            case ConnectivityManager.TYPE_ETHERNET:
                mThreadCount = 4;
                mMaxThreadCount = 4;
                return;

            case ConnectivityManager.TYPE_MOBILE:
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_LTE:  // 4G
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        mThreadCount = 3;
                        mMaxThreadCount = 3;
                        return;

                    case TelephonyManager.NETWORK_TYPE_UMTS: // 3G
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        mThreadCount = 2;
                        mMaxThreadCount = 2;
                        return;

                    case TelephonyManager.NETWORK_TYPE_GPRS: // 2G
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        mThreadCount = 1;
                        mMaxThreadCount = 1;
                        return;

                    default:
                        mThreadCount = 1;
                        mMaxThreadCount = 1;
                        return;
                }

            default:
                mThreadCount = 1;
                mMaxThreadCount = 1;
                return;
        }
    }

    @Override
    public synchronized void execute(final IRequest request) {
        mPausableThreadPoolExecutor.execute(request);
    }

    @Override
    public synchronized void setPaused(final boolean paused) {
        if (paused && !mPausableThreadPoolExecutor.isPaused()) {
            mPausableThreadPoolExecutor.setState(Lifecycle.STATE_PAUSE);
        }

        if (!paused && mPausableThreadPoolExecutor.isPaused()) {
            if (ApplicationController.getInstance() != null) {
                setThreadCount(Connectivity.getActiveNetworkInfo(ApplicationController.getInstance()));
            }
            mPausableThreadPoolExecutor.setCorePoolSize(mThreadCount);
            mPausableThreadPoolExecutor.setMaximumPoolSize(mMaxThreadCount);
            mPausableThreadPoolExecutor.setState(Lifecycle.STATE_RESUME);
        }
    }


}
