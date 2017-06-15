package com.cleanarchitecture.shishkin.api.mail;

import android.location.Location;

import com.cleanarchitecture.shishkin.api.controller.ILocationSubscriber;
import com.cleanarchitecture.shishkin.api.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.api.ui.activity.AbstractActivity;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;

public class SetLocationMail extends AbstractMail {

    private static final String NAME = SetLocationMail.class.getName();
    private Location mLocation;

    public SetLocationMail(final String address, final Location location)
    {
        super(address);

        mLocation = location;
    }

    @Override
    public void read(final IMailSubscriber subscriber) {
        if (subscriber instanceof ILocationSubscriber) {
            ((ILocationSubscriber)subscriber).setLocation(mLocation);
        }
    }

    @Override
    public boolean isCheckDublicate() {
        return true;
    }

    @Override
    public String getName() {
        return NAME;
    }

}
