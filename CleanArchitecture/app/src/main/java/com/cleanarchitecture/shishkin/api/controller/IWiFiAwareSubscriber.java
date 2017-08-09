package com.cleanarchitecture.shishkin.api.controller;

import android.net.wifi.p2p.WifiP2pDevice;

public interface IWiFiAwareSubscriber extends ISubscriber {

    void onP2pOn();

    void onP2pOff();

    void onP2pDeviceChanged(WifiP2pDevice device);

}
