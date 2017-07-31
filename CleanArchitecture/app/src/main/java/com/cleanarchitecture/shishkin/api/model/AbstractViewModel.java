package com.cleanarchitecture.shishkin.api.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.cleanarchitecture.shishkin.api.controller.ISubscriber;

public abstract class AbstractViewModel<T> extends AndroidViewModel implements ISubscriber {

    public AbstractViewModel(Application application) {
        super(application);

        setLiveData();
    }

    /**
     * Подписаться на LiveData
     */
    public abstract void setLiveData();

    /**
     * Получить LiveData
     *
     * @return LiveData
     */
    public abstract LiveData<T> getLiveData();
}
