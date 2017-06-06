package com.cleanarchitecture.shishkin.base.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;

import com.cleanarchitecture.shishkin.base.controller.ISubscribeable;
import com.cleanarchitecture.shishkin.base.event.OnNetworkConnectedEvent;
import com.cleanarchitecture.shishkin.base.event.OnNetworkDisconnectedEvent;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;

/**
 * A helpful implementation of {@link ISubscribeable} that receives network state changes.
 */
public class ConnectivityMonitor extends BroadcastReceiver implements ISubscribeable {

    //private static final String EXTRA_AIRPLANE_STATE = "state";
    private boolean mSubscribed = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void subscribe(@NonNull final Context context) {
        if (!mSubscribed) {
            final IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(this, filter);
            mSubscribed = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unsubscribe(@NonNull final Context context) {
        if (mSubscribed) {
            context.unregisterReceiver(this);
            mSubscribed = false;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void onReceive(final Context context, final Intent intent) {
        // On some versions of Android this may be called with a null Intent,
        // also without extras (getExtras() == null), in such case we use defaults.
        if (intent == null) {
            return;
        }

        final String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            if (Connectivity.isNetworkConnected(context)) {
                ApplicationUtils.postEvent(new OnNetworkConnectedEvent());
            } else {
                ApplicationUtils.postEvent(new OnNetworkDisconnectedEvent());
            }
        }
    }
}
