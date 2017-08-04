package com.cleanarchitecture.shishkin.api.controller;

import android.location.Location;

public interface ILocationSubscriber extends ISubscriber {
    /**
     * Установить у подписчика текущий Location
     *
     * @param location текущий Location
     */
    void setLocation(Location location);
}
