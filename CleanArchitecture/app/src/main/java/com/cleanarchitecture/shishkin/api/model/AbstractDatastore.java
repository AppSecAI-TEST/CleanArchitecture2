package com.cleanarchitecture.shishkin.api.model;

import com.cleanarchitecture.shishkin.api.controller.EventBusController;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

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
        return StringUtils.arrayToList(EventBusController.NAME);
    }

}
