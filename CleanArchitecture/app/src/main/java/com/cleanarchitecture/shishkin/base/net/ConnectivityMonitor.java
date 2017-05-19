package com.cleanarchitecture.shishkin.base.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;

import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.controller.ISubscribeable;
import com.cleanarchitecture.shishkin.base.event.OnNetworkConnectedEvent;
import com.cleanarchitecture.shishkin.base.event.OnNetworkDisconnectedEvent;

/**
 * A helpful implementation of {@link ISubscribeable} that receives network state changes.
 */
public class ConnectivityMonitor extends BroadcastReceiver implements ISubscribeable {

    private static final String EXTRA_AIRPLANE_STATE = "state";
    private boolean mSubscribed = false;

    /**
     * Creates a connectivity monitor with a given connectivity listener.
     */
    public ConnectivityMonitor() {
    }

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
        if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(action)) {
            //if (!intent.hasExtra(EXTRA_AIRPLANE_STATE)) {
            //	return; // No airplane state, ignore it. Should we query Utils.isAirplaneModeOn?
            //}
            // TODO: dispatcher.dispatchAirplaneModeChange(intent.getBooleanExtra(EXTRA_AIRPLANE_STATE, false));

        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            checkState(context);
        }
    }

    private synchronized void checkState(final Context context) {
        if (context != null) {
            if (Connectivity.isNetworkConnected(context)) {
                EventController.getInstance().post(new OnNetworkConnectedEvent());
            } else {
                EventController.getInstance().post(new OnNetworkDisconnectedEvent());
            }
        }
    }

}
