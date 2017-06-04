package com.cleanarchitecture.shishkin.base.room;

import com.cleanarchitecture.shishkin.base.controller.Controllers;
import com.cleanarchitecture.shishkin.base.observer.Debounce;

import java.util.concurrent.TimeUnit;

public class ViewModelDebounce extends Debounce {

    private String mName;

    public ViewModelDebounce(final String name) {
        super(TimeUnit.MINUTES.toMillis(5), 0);

        mName = name;
    }

    @Override
    public void run() {
        if (Controllers.getInstance() != null) {
            Controllers.getInstance().getDbProvider().removeViewModel(mName);
        }
    }
}
