package com.cleanarchitecture.shishkin.base.event.database;

import com.cleanarchitecture.shishkin.base.event.AbstractEvent;

/**
 * Событие - БД создана
 */
public class DbCreatedEvent extends AbstractEvent {

    private String mDbName = null;

    public DbCreatedEvent (final String name) {
        mDbName = name;
    }

    public String getName() {
        return mDbName;
    }
}
