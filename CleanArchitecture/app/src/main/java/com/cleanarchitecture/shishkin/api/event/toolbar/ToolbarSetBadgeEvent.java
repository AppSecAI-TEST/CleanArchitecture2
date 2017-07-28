package com.cleanarchitecture.shishkin.api.event.toolbar;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

/**
 * Событие - установить Badge
 */
public class ToolbarSetBadgeEvent extends AbstractEvent {

    private String mCount;
    private boolean mVisible;

    public ToolbarSetBadgeEvent(final String count, final boolean isVisible) {
        mCount = count;
        mVisible = isVisible;
    }

    public String getCount() {
        return mCount;
    }

    public boolean isVisible() {
        return mVisible;
    }

}
