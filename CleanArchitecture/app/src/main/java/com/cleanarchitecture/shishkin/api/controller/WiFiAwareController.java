package com.cleanarchitecture.shishkin.api.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;

import java.lang.ref.WeakReference;

public class WiFiAwareController extends AbstractController<IWiFiAwareSubscriber> {

    public static final String NAME = WiFiAwareController.class.getName();

    private boolean isEnabled = false;
    private BroadcastReceiver mWiFip2pReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (!hasSubscribers()) {
                return;
            }

            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                // Determine if Wifi P2P mode is enabled or not, alert
                // the Activity.
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    for (WeakReference<IWiFiAwareSubscriber> subscriber : getSubscribers().values()) {
                        if (subscriber != null && subscriber.get() != null) {
                            subscriber.get().onP2pOn();
                        }
                    }
                } else {
                    for (WeakReference<IWiFiAwareSubscriber> subscriber : getSubscribers().values()) {
                        if (subscriber != null && subscriber.get() != null) {
                            subscriber.get().onP2pOff();
                        }
                    }
                }
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

                // The peer list has changed!  We should probably do something about
                // that.

            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

                // Connection state changed!  We should probably do something about
                // that.

            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                final WifiP2pDevice device = (WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                for (WeakReference<IWiFiAwareSubscriber> subscriber : getSubscribers().values()) {
                    if (subscriber != null && subscriber.get() != null) {
                        subscriber.get().onP2pDeviceChanged(device);
                    }
                }
            }
        }
    };

    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager mManager;

    public WiFiAwareController() {
        if (ApplicationUtils.hasO()) {
            final Context context = ApplicationController.getInstance();
            if (context != null) {
                isEnabled = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE);

                mManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
                mChannel = mManager.initialize(context, context.getMainLooper(), null);
            }
        }
    }

    @Override
    public synchronized void onRegisterFirstSubscriber() {
        startP2p();
    }

    private synchronized void startP2p() {
        if (isEnabled()) {
            final Context context = ApplicationController.getInstance();
            if (context != null) {
                final IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
                intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
                intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
                intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

                context.registerReceiver(mWiFip2pReceiver, intentFilter);
            }
        }
    }

    @Override
    public synchronized void onUnRegisterLastSubscriber() {
        stopP2p();
    }

    private synchronized void stopP2p() {
        if (mWiFip2pReceiver != null) {
            final Context context = ApplicationController.getInstance();
            if (context != null) {
                context.unregisterReceiver(mWiFip2pReceiver);
            }
        }
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        final Context context = ApplicationController.getInstance();
        if (context != null) {
            return context.getString(R.string.module_wifi_aware);
        }
        return "Wi-Fi aware module";
    }
}
