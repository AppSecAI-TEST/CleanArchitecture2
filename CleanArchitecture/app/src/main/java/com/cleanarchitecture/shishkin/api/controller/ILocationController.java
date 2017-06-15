package com.cleanarchitecture.shishkin.api.controller;

import android.location.Location;

public interface ILocationController extends IController<ILocationSubscriber> {

    /**
     * Получить текущее положение
     *
     * @return текущее положение
     */
    Location getLocation();

}
