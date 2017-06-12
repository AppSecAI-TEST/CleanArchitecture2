package com.cleanarchitecture.shishkin.base.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DesktopController extends AbstractSmallController<IDesktopSubscriber> implements IDesktopController<IDesktopSubscriber> {
    public static final String NAME = "DesktopController";
    public static final String SUBSCRIBER_TYPE = "IDesktopSubscriber";

    private String mDesktop = "default"; // default desktop
    private List<String> mDesktops = Collections.synchronizedList(new ArrayList<>());

    public DesktopController() {
        registerDesktop(mDesktop);
    }

    @Override
    public synchronized String getDesktop() {
        return mDesktop;
    }

    public synchronized void setDesktop(String desktop) {
        mDesktop = desktop;
    }

    public synchronized void registerDesktop(String desktop) {
        if (!mDesktops.contains(desktop)) {
            mDesktops.add(desktop);
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

}
