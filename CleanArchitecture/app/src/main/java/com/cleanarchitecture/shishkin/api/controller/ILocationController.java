package com.cleanarchitecture.shishkin.api.controller;

import android.location.Address;
import android.location.Location;

import java.util.List;

public interface ILocationController extends IController<ILocationSubscriber> {


    /**
     * запустить службу геолокации
     *
     */
    void startLocation();

    /**
     * Получить текущее положение
     *
     * @return текущее положение
     */
    Location getLocation();

    /**
     * Получить список адресов по его месту
     *
     * @param location     location
     * @param countAddress кол-во адресов
     * @return список адресов
     */
    List<Address> getAddress(Location location, int countAddress);

}
