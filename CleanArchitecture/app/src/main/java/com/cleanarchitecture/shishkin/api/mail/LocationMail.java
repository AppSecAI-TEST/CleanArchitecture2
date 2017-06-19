package com.cleanarchitecture.shishkin.api.mail;

import android.location.Location;
import android.widget.Toast;

import com.cleanarchitecture.shishkin.api.controller.ActivityController;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.ILocationSubscriber;
import com.cleanarchitecture.shishkin.api.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.api.event.ui.ShowToastEvent;

public class LocationMail extends AbstractMail {

    private static final String NAME = LocationMail.class.getName();

    private Location mLocation;

    public LocationMail(final String address, final Location location) {
        super(address);

        mLocation = location;
    }

    public Location getLocation() {
        return mLocation;
    }

    @Override
    public void read(final IMailSubscriber subscriber) {
        if (subscriber instanceof ILocationSubscriber) {
            ((ILocationSubscriber) subscriber).setLocation(getLocation());
        }
    }

    @Override
    public IMail copy() {
        return new LocationMail(getAddress(), getLocation());
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isCheckDublicate() {
        return true;
    }
}
