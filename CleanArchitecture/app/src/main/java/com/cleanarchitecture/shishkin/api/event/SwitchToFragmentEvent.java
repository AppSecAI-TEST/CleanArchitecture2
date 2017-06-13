package com.cleanarchitecture.shishkin.api.event;

/**
 * Событие - выполнить команду "переключиться на фрагмент"
 */
public class SwitchToFragmentEvent extends AbstractEvent {
    private String mName;

    public SwitchToFragmentEvent(final String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

}
