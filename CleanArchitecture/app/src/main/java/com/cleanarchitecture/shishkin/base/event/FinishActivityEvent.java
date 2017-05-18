package com.cleanarchitecture.shishkin.base.event;

/**
 * Событие - выполнить команду "закрыть заданную activity"
 */
public class FinishActivityEvent extends AbstractEvent {
    private String mName;

    public FinishActivityEvent(final String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }
}
