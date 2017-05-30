package com.cleanarchitecture.shishkin.base.repository;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.event.OnNetworkConnectedEvent;
import com.cleanarchitecture.shishkin.base.event.OnNetworkDisconnectedEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowMessageEvent;
import com.cleanarchitecture.shishkin.base.net.Connectivity;
import com.cleanarchitecture.shishkin.base.net.ConnectivityMonitor;
import com.cleanarchitecture.shishkin.base.repository.requests.IRequest;
import com.cleanarchitecture.shishkin.base.task.IPhonePausableThreadPoolExecutor;
import com.cleanarchitecture.shishkin.base.task.PhonePausableThreadPoolExecutor;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

public class NetProvider implements INetProvider {
    public static final String NAME = "NetProvider";

    private static volatile NetProvider sInstance;
    private boolean mConnected = false;
    private ConnectivityMonitor mConnectivityMonitor;
    private IPhonePausableThreadPoolExecutor mPhonePausableThreadPoolExecutor;

    public static synchronized void instantiate() {
        if (sInstance == null) {
            synchronized (NetProvider.class) {
                if (sInstance == null) {
                    sInstance = new NetProvider();
                }
            }
        }
    }

    public static NetProvider getInstance() {
        instantiate();
        return sInstance;
    }

    private NetProvider () {
        final Context context = ApplicationController.getInstance();
        if (context == null) {
            return;
        }

        EventController.getInstance().register(this);

        mConnectivityMonitor = new ConnectivityMonitor();
        mConnectivityMonitor.subscribe(context);

        mConnected = Connectivity.isNetworkConnected(context);
        mPhonePausableThreadPoolExecutor = new PhonePausableThreadPoolExecutor(context, 4, TimeUnit.MINUTES);
    }

    @Override
    public synchronized void request(final IRequest request) {
        if (mConnected) {
            mPhonePausableThreadPoolExecutor.execute(request);
        }
    }

    @Override
    public synchronized void setPaused(boolean paused) {
        mConnected = !paused;
        mPhonePausableThreadPoolExecutor.setPaused(paused);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onNetworkConnectedEvent(OnNetworkConnectedEvent event) {
        setPaused(false);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onNetworkDisconnectedEvent(OnNetworkDisconnectedEvent event) {
        setPaused(true);

        final Context context = ApplicationController.getInstance();
        if (context != null) {
            EventController.getInstance().post(new ShowMessageEvent(context.getString(R.string.network_disconnected)));
        }
    }



}

