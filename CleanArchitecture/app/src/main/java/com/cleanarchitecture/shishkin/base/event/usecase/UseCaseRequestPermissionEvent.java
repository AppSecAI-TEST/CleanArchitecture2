package com.cleanarchitecture.shishkin.base.event.usecase;

import com.cleanarchitecture.shishkin.base.event.AbstractEvent;

/**
 * Событие - запросить право приложению
 */
public class UseCaseRequestPermissionEvent extends AbstractEvent {
    private String mName;

    public UseCaseRequestPermissionEvent(final String name){
        mName = name;
    }

    public String getPermission() {
        return mName;
    }
}
