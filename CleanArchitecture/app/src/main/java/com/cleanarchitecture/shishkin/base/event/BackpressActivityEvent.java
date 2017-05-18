package com.cleanarchitecture.shishkin.base.event;

/**
 * Событие - выполнить команду "нажатие Backpress указанной activity"
 */
public class BackpressActivityEvent extends AbstractEvent {
    private String mName;

    public BackpressActivityEvent(final String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }
}
