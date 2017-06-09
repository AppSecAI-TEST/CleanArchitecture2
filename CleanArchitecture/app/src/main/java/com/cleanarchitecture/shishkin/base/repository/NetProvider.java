package com.cleanarchitecture.shishkin.base.repository;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.base.controller.EventBusController;
import com.cleanarchitecture.shishkin.base.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.base.event.OnNetworkConnectedEvent;
import com.cleanarchitecture.shishkin.base.event.OnNetworkDisconnectedEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowMessageEvent;
import com.cleanarchitecture.shishkin.base.net.Connectivity;
import com.cleanarchitecture.shishkin.base.net.ConnectivityMonitor;
import com.cleanarchitecture.shishkin.base.repository.requests.IRequest;
import com.cleanarchitecture.shishkin.base.task.IPhonePausableThreadPoolExecutor;
import com.cleanarchitecture.shishkin.base.task.PhonePausableThreadPoolExecutor;
import com.cleanarchitecture.shishkin.base.utils.AdminUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NetProvider implements INetProvider, IModuleSubscriber {
    public static final String NAME = "NetProvider";

    private boolean mConnected = false;
    private ConnectivityMonitor mConnectivityMonitor;
    private IPhonePausableThreadPoolExecutor mPhonePausableThreadPoolExecutor;

    public NetProvider() {
        final Context context = AdminUtils.getContext();

        if (context != null) {
            mConnectivityMonitor = new ConnectivityMonitor();
            mConnectivityMonitor.subscribe(context);

            mConnected = Connectivity.isNetworkConnected(context);
            mPhonePausableThreadPoolExecutor = new PhonePausableThreadPoolExecutor(4, TimeUnit.MINUTES);
        }
    }

    @Override
    public synchronized void request(final IRequest request) {
        if (mConnected && mPhonePausableThreadPoolExecutor != null) {
            mPhonePausableThreadPoolExecutor.execute(request);
        }
    }

    @Override
    public synchronized void setPaused(boolean paused) {
        mConnected = !paused;

        if (mPhonePausableThreadPoolExecutor != null) {
            mPhonePausableThreadPoolExecutor.setPaused(paused);
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

    @Override
    public List<String> hasSubscriberType() {
        final ArrayList<String> list = new ArrayList<>();
        list.add(EventBusController.SUBSCRIBER_TYPE);
        return list;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onNetworkConnectedEvent(OnNetworkConnectedEvent event) {
        setPaused(false);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onNetworkDisconnectedEvent(OnNetworkDisconnectedEvent event) {
        setPaused(true);

        final Context context = AdminUtils.getContext();
        if (context != null) {
            AdminUtils.postEvent(new ShowMessageEvent(context.getString(R.string.network_disconnected)));
        }
    }

}

