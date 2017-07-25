package com.cleanarchitecture.shishkin.api.model;

import com.cleanarchitecture.shishkin.api.controller.IModuleSubscriber;

public interface IDatastore extends IModuleSubscriber {

    void getData();

    void terminate();

    void clearData();

    void onChangeData();
}
