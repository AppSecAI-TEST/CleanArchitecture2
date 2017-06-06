package com.cleanarchitecture.shishkin.base.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.cleanarchitecture.shishkin.base.controller.ISubscriber;

public abstract class AbstractViewModel<T> extends AndroidViewModel implements ISubscriber {

    public AbstractViewModel(Application application) {
        super(application);

        subscribe();
    }

    /**
     * Подписаться на LiveData
     */
    public abstract void subscribe();

    /**
     * Получить LiveData
     *
     * @return LiveData
     */
    public abstract LiveData<T> getLiveData();
}
