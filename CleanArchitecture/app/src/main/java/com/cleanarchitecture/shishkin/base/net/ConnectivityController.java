package com.cleanarchitecture.shishkin.base.net;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.event.OnNetworkConnectedEvent;
import com.cleanarchitecture.shishkin.base.event.OnNetworkDisconnectedEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowMessageEvent;
import com.cleanarchitecture.shishkin.base.repository.NetProvider;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ConnectivityController {

    private static volatile ConnectivityController sInstance;
    private boolean mConnected = false;
    private static ConnectivityMonitor mConnectivityMonitor;

    public static void instantiate() {
        if (sInstance == null) {
            synchronized (ConnectivityController.class) {
                if (sInstance == null) {
                    sInstance = new ConnectivityController();
                }
            }
        }
    }

    public static ConnectivityController getInstance() {
        instantiate();
        return sInstance;
    }

    private ConnectivityController() {
        EventController.getInstance().register(this);

        final Context context = ApplicationController.getInstance();
        if (context != null) {
            mConnectivityMonitor = new ConnectivityMonitor();
            mConnectivityMonitor.subscribe(context);
        }
    }

    public synchronized boolean isConnected() {
        return mConnected;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onNetworkDisconnectedEvent(OnNetworkDisconnectedEvent event) {
        mConnected = false;

        NetProvider.getInstance().setPaused(true);

        final Context context = ApplicationController.getInstance();
        if (context != null) {
            EventController.getInstance().post(new ShowMessageEvent(context.getString(R.string.network_disconnected)));
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onNetworkConnectedEvent(OnNetworkConnectedEvent event) {
        mConnected = true;

        NetProvider.getInstance().setPaused(false);
    }


}
