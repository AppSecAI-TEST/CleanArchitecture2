package com.cleanarchitecture.shishkin.api.controller;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;

import com.cleanarchitecture.shishkin.api.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.api.event.OnPermisionGrantedEvent;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LocationController extends AbstractController<ILocationSubscriber> implements IModuleSubscriber {
    public static final String NAME = LocationController.class.getName();
    public static final String SUBSCRIBER_TYPE = ILocationSubscriber.class.getName();
    private static final String LOG_TAG = "LocationController:";

    private static final long POLLING_FREQ = TimeUnit.MINUTES.toMillis(5);
    private static final long FASTEST_UPDATE_FREQ = TimeUnit.SECONDS.toMillis(30);

    private FusedLocationProviderClient mFusedLocationClient;
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

        startLocation();
    }

    private void startLocation() {
        if (AdminUtils.isGooglePlayServices()) {
            if (AdminUtils.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                final Context context = AdminUtils.getContext();
                if (context != null) {
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    ErrorController.getInstance().onError(LOG_TAG, e.getMessage());
                                }
                            });
                }
            }
        }
    }

    public void setLocation(final Location mLocation) {
        this.mLocation = mLocation;

        for (WeakReference<ILocationSubscriber> ref : getSubscribers().values()) {
            if (ref != null && ref.get() != null) {
                ref.get().setLocation(mLocation);
            }
        }
    }

    public Location getLocation() {
        return mLocation;
    }

    @Override
    public synchronized void register(final ILocationSubscriber subscriber) {
        super.register(subscriber);

        if (subscriber != null && mLocation != null) {
            subscriber.setLocation(mLocation);
        }
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

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public synchronized void onPermisionGrantedEvent(OnPermisionGrantedEvent event) {
        if (event.getPermission().equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
            startLocation();
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public synchronized void onFinishApplicationEvent(FinishApplicationEvent event) {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }


}
