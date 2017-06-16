package com.cleanarchitecture.shishkin.api.controller;

import android.Manifest;
import android.content.Context;
import android.location.Location;

import com.cleanarchitecture.shishkin.api.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.api.event.OnPermisionGrantedEvent;
import com.cleanarchitecture.shishkin.api.event.OnScreenOffEvent;
import com.cleanarchitecture.shishkin.api.event.OnScreenOnEvent;
import com.cleanarchitecture.shishkin.api.mail.SetLocationMail;
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
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class LocationController extends AbstractController<ILocationSubscriber> implements ILocationController, IModuleSubscriber {
    public static final String NAME = LocationController.class.getName();
    public static final String SUBSCRIBER_TYPE = ILocationSubscriber.class.getName();
    private static final String LOG_TAG = "LocationController:";

    private static final long POLLING_FREQ = TimeUnit.MINUTES.toMillis(1);
    private static final long FASTEST_UPDATE_FREQ = TimeUnit.SECONDS.toMillis(10);
    private static final float SMALLEST_DISPLACEMENT = 100F;

    private FusedLocationProviderClient mFusedLocationClient = null;
    private Location mLocation = null;
    private LocationCallback mLocationCallback = null;
    private LocationRequest mLocationRequest = null;

    public LocationController() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                final List<Location> list = locationResult.getLocations();
                if (!list.isEmpty()) {
                    setLocation(list.get(list.size() - 1));
                }
            }
        };

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(POLLING_FREQ);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_FREQ);
        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);
    }

    private void startLocation() {
        if (mFusedLocationClient != null) {
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

        final Context context = AdminUtils.getContext();
        if (context == null) {
            return;
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
                .addOnFailureListener(e -> ErrorController.getInstance().onError(LOG_TAG, e.getMessage()));
    }

    private void stopLocation(boolean isforce) {
        if (isforce || (!isforce && !hasSubscribers())) {
            if (mFusedLocationClient != null) {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                mFusedLocationClient = null;
            }
        }
    }

    private void setLocation(final Location mLocation) {
        this.mLocation = mLocation;

        if (hasSubscribers()) {
            for (WeakReference<ILocationSubscriber> ref : getSubscribers().values()) {
                if (ref != null && ref.get() != null) {
                    if (ref.get() instanceof IMailSubscriber) {
                        AdminUtils.addMail(new SetLocationMail(ref.get().getName(), mLocation));
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
    public synchronized void register(final ILocationSubscriber subscriber) {
        super.register(subscriber);

        startLocation();
    }

    @Override
    public synchronized void unregister(final ILocationSubscriber subscriber) {
        super.unregister(subscriber);

        stopLocation(false);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return SUBSCRIBER_TYPE;
    }

    @Override
    public List<String> hasSubscriberType() {
        final ArrayList<String> list = new ArrayList<>();
        list.add(EventBusController.SUBSCRIBER_TYPE);
        return list;
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public void onUnRegister() {
        stopLocation(true);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public synchronized void onPermisionGrantedEvent(OnPermisionGrantedEvent event) {
        if (event.getPermission().equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
            startLocation();
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public synchronized void onFinishApplicationEvent(FinishApplicationEvent event) {
        stopLocation(true);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onScreenOffEvent(final OnScreenOffEvent event) {
        stopLocation(true);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onScreenOnEvent(final OnScreenOnEvent event) {
        startLocation();
    }
}