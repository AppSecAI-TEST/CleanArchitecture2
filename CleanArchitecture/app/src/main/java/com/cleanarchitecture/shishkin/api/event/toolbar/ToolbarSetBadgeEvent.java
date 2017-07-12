package com.cleanarchitecture.shishkin.api.event.toolbar;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

/**
 * Событие - установить Badge
 */
public class ToolbarSetBadgeEvent extends AbstractEvent {

    private int mCount;
    private boolean mVisible;

    public ToolbarSetBadgeEvent(final int count, final boolean isVisible) {
        mCount = count;
        mVisible = isVisible;
    }

    public int getCount() {
        return mCount;
    }

    public boolean isVisible() {
        return mVisible;
    }

}
