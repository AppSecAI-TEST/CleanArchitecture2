package com.cleanarchitecture.shishkin.base.room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.cleanarchitecture.shishkin.base.controller.ISubscriber;

public abstract class AbstractViewModel<T> extends AndroidViewModel implements ISubscriber {

    public AbstractViewModel(Application application) {
        super(application);

        subscribe();
    }

    public abstract void subscribe();

    public abstract LiveData<T> getLiveData();
}
