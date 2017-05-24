package com.cleanarchitecture.shishkin.base.net;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;

/**
 * Commonly used network kinds.
 */
public enum NetworkKind {

    NONE,
    MOBILE,
    MOBILE_3G,
    MOBILE_4G,
    WIFI;

    /**
     * Returns a connectivity kind from a given network info.
     *
     * @param networkInfo The status of a network interface that represents
     *                    the current network connection.
     */
    @NonNull
    public static NetworkKind valueOf(@Nullable final NetworkInfo networkInfo) {
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
            return NONE;
        }

        switch (networkInfo.getType()) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_WIMAX:
            case ConnectivityManager.TYPE_ETHERNET:
                return WIFI;

            case ConnectivityManager.TYPE_MOBILE:
                switch (networkInfo.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_LTE:  // 4G
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    //case TelephonyManager.NETWORK_TYPE_IWLAN:
                        return MOBILE_4G;

                    case TelephonyManager.NETWORK_TYPE_UMTS: // 3G
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    //case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                        return MOBILE_3G;

                    case TelephonyManager.NETWORK_TYPE_GPRS: // 2G
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                    //case TelephonyManager.NETWORK_TYPE_GSM:
                        return MOBILE;

                    default:
                        return MOBILE;
                }

            case ConnectivityManager.TYPE_BLUETOOTH:
                return MOBILE;

            default:
                return MOBILE;
        }
    }

}
