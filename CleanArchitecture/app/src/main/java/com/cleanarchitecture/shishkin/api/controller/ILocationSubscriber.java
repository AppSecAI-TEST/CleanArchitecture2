package com.cleanarchitecture.shishkin.api.controller;

import android.location.Location;

public interface ILocationSubscriber extends ISubscriber {
    void setLocation(Location location);
}
