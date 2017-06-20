package com.cleanarchitecture.shishkin.api.data;

import com.cleanarchitecture.shishkin.api.controller.Admin;
import com.cleanarchitecture.shishkin.api.debounce.Debounce;
import com.cleanarchitecture.shishkin.api.repository.DbProvider;
import com.cleanarchitecture.shishkin.api.repository.IDbProvider;

import java.util.concurrent.TimeUnit;

public class ViewModelDebounce extends Debounce {

    private String mName;

    public ViewModelDebounce(final String name) {
        super(TimeUnit.SECONDS.toMillis(20), 0);

        mName = name;
    }

    @Override
    public void run() {
        if (Admin.getInstance() != null) {
            final IDbProvider provider = Admin.getInstance().get(DbProvider.NAME);
            if (provider != null) {
                provider.removeViewModel(mName);
            }
        }
    }
}
