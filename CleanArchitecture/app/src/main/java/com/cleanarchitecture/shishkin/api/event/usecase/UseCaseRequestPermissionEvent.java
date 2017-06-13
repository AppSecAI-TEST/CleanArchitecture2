package com.cleanarchitecture.shishkin.api.event.usecase;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

/**
 * Событие - запросить право приложению
 */
public class UseCaseRequestPermissionEvent extends AbstractEvent {
    private String mName;

    public UseCaseRequestPermissionEvent(final String name) {
        mName = name;
    }

    public String getPermission() {
        return mName;
    }
}
