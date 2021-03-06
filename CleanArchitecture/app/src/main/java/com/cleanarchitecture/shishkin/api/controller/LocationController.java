package com.cleanarchitecture.shishkin.api.controller;

import android.Manifest;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.api.event.OnBackgroundOffEvent;
import com.cleanarchitecture.shishkin.api.event.OnBackgroundOnEvent;
import com.cleanarchitecture.shishkin.api.event.OnNetworkConnectedEvent;
import com.cleanarchitecture.shishkin.api.event.OnNetworkDisconnectedEvent;
import com.cleanarchitecture.shishkin.api.event.OnPermisionGrantedEvent;
import com.cleanarchitecture.shishkin.api.event.OnScreenOffEvent;
import com.cleanarchitecture.shishkin.api.event.OnScreenOnEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowDialogEvent;
import com.cleanarchitecture.shishkin.api.mail.LocationMail;
import com.cleanarchitecture.shishkin.common.net.Connectivity;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class LocationController extends AbstractController<ILocationSubscriber> implements ILocationController, IModuleSubscriber {
    public static final String NAME = LocationController.class.getName();
    private static final String LOG_TAG = "LocationController:";

    private static final long POLLING_FREQ = TimeUnit.MINUTES.toMillis(1);
    private static final long FASTEST_UPDATE_FREQ = TimeUnit.SECONDS.toMillis(10);
    private static final float SMALLEST_DISPLACEMENT = 100F;

    private FusedLocationProviderClient mFusedLocationClient = null;
    private Location mLocation = null;
    private LocationCallback mLocationCallback = null;
    private LocationRequest mLocationRequest = null;
    private Geocoder mGeocoder;

    public LocationController() {
        if (PreferencesModule.getInstance().getModule(LocationController.NAME)) {
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    setLocation(locationResult.getLastLocation());
                }
            };

            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            mLocationRequest.setInterval(POLLING_FREQ);
            mLocationRequest.setFastestInterval(FASTEST_UPDATE_FREQ);
            mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);

            final Context context = ApplicationController.getInstance();
            if (context != null) {
                mGeocoder = new Geocoder(context, Locale.getDefault());
            }
        }
    }

    @Override
    public synchronized void startLocation() {
        if (mFusedLocationClient != null) {
            return;
        }

        if (!PreferencesModule.getInstance().getModule(LocationController.NAME)) {
            return;
        }

        if (!hasSubscribers()) {
            return;
        }

        if (!AdminUtils.isGooglePlayServices()) {
            return;
        }

        if (!AdminUtils.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            return;
        }

        final Context context = ApplicationController.getInstance();
        if (context == null) {
            return;
        }

        if (!ApplicationUtils.isLocationEnabled(context)) {
            AdminUtils.postEvent(new ShowDialogEvent(R.id.dialog_enable_location, context.getString(R.string.warning), context.getString(R.string.enable_location)));
            return;
        }

        if (mLocationRequest != null && mLocationCallback != null) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
                    .addOnFailureListener(e -> ErrorController.getInstance().onError(LOG_TAG, e.getMessage(), false));
        }

        if (mLocation != null) {
            setLocation(mLocation);
        }
    }

    public synchronized void stopLocation(final boolean isforce) {
        if (isforce || (!isforce && !hasSubscribers())) {
            if (mFusedLocationClient != null) {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                mFusedLocationClient = null;
            }
        }
    }

    private synchronized void setLocation(final Location location) {
        this.mLocation = location;

        if (hasSubscribers()) {
            for (WeakReference<ILocationSubscriber> ref : getSubscribers().values()) {
                if (ref != null && ref.get() != null) {
                    if (ref.get() instanceof IMailSubscriber) {
                        AdminUtils.addMail(new LocationMail(ref.get().getName(), mLocation));
                    } else {
                        ref.get().setLocation(mLocation);
                    }
                }
            }
        }
    }

    @Override
    public Location getLocation() {
        return mLocation;
    }

    @Override
    public synchronized void onRegisterFirstSubscriber() {
        startLocation();
    }

    @Override
    public synchronized void onUnRegisterLastSubscriber() {
        stopLocation(false);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<String> getSubscription() {
        return StringUtils.arrayToList(EventBusController.NAME);
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public void onUnRegisterModule() {
        stopLocation(true);
    }

    @Override
    public synchronized List<Address> getAddress(final Location location, final int countAddress) {
        int cnt = countAddress;
        if (cnt < 1) {
            cnt = 1;
        }

        final List<Address> list = new ArrayList<>();

        final Context context = AdminUtils.getContext();
        if (context == null) {
            return list;
        }

        if (AdminUtils.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (Connectivity.isNetworkConnected(context)) {
                if (mGeocoder != null && mFusedLocationClient != null) {
                    if (Geocoder.isPresent()) {
                        try {
                            list.addAll(mGeocoder.getFromLocation(
                                    location.getLatitude(),
                                    location.getLongitude(),
                                    cnt));
                        } catch (Exception e) {
                            ErrorController.getInstance().onError(LOG_TAG, e.getMessage(), false);
                        }
                    }
                }
            }
        }
        return list;
    }

    @Override
    public String getDescription() {
        final Context context = ApplicationController.getInstance();
        if (context != null) {
            return context.getString(R.string.module_location);
        }
        return "Location controller";
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onPermisionGrantedEvent(OnPermisionGrantedEvent event) {
        if (event.getPermission().equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
            startLocation();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinishApplicationEvent(FinishApplicationEvent event) {
        stopLocation(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScreenOffEvent(final OnScreenOffEvent event) {
        stopLocation(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScreenOnEvent(final OnScreenOnEvent event) {
        startLocation();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkConnectedEvent(final OnNetworkConnectedEvent event) {
        startLocation();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkDisconnectedEvent(final OnNetworkDisconnectedEvent event) {
        stopLocation(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackgroundOnEvent(final OnBackgroundOnEvent event) {
        if (ApplicationUtils.hasO()) {
            stopLocation(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackgroundOffEvent(final OnBackgroundOffEvent event) {
        if (ApplicationUtils.hasO()) {
            startLocation();
        }
    }
}
