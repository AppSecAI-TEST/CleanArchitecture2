package com.cleanarchitecture.shishkin.api.model;

import com.cleanarchitecture.shishkin.api.controller.EventBusController;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDatastore<T extends ILiveData> implements IDatastore<T> {

    private T mData;

    public AbstractDatastore(T data) {
        mData = data;
    }

    public T getLiveData() {
        return mData;
    }

    @Override
    public List<String> getSubscription() {
        final ArrayList<String> list = new ArrayList<>();
        list.add(EventBusController.NAME);
        return list;
    }

}
