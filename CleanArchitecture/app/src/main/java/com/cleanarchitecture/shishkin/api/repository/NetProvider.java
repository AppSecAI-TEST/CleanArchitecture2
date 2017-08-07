package com.cleanarchitecture.shishkin.api.repository;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AbstractModule;
import com.cleanarchitecture.shishkin.api.controller.ActivityController;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.EventBusController;
import com.cleanarchitecture.shishkin.api.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.api.event.OnNetworkConnectedEvent;
import com.cleanarchitecture.shishkin.api.event.OnNetworkDisconnectedEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowMessageEvent;
import com.cleanarchitecture.shishkin.api.net.ConnectivityMonitor;
import com.cleanarchitecture.shishkin.api.repository.requests.IRequest;
import com.cleanarchitecture.shishkin.api.task.IPhonePausableThreadPoolExecutor;
import com.cleanarchitecture.shishkin.api.task.PhonePausableThreadPoolExecutor;
import com.cleanarchitecture.shishkin.common.net.Connectivity;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class NetProvider extends AbstractModule implements INetProvider, IModuleSubscriber {
    public static final String NAME = NetProvider.class.getName();

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
    public List<String> hasSubscriberType() {
        return StringUtils.arrayToList(EventBusController.NAME);
    }

    @Override
    public String getDescription() {
        return "Net Provider Module";
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
            AdminUtils.postEvent(new ShowMessageEvent(context.getString(R.string.network_disconnected)).setType(ActivityController.TOAST_TYPE_WARNING));
        }
    }

}

