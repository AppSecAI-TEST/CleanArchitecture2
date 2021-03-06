package com.cleanarchitecture.shishkin.api.event.database;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

/**
 * Событие - БД обновлена
 */
public class DbUpdatedEvent extends AbstractEvent {

    private String mDbName = null;

    public DbUpdatedEvent(final String name) {
        mDbName = name;
    }

    public String getName() {
        return mDbName;
    }
}
