package com.cleanarchitecture.shishkin.api.event.database;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

/**
 * Событие - БД создана
 */
public class DbCreatedEvent extends AbstractEvent {

    private String mDbName = null;

    public DbCreatedEvent(final String name) {
        mDbName = name;
    }

    public String getName() {
        return mDbName;
    }
}
