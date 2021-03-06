package com.cleanarchitecture.shishkin.api.event.toolbar;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

/**
 * Событие - установить item Toolbar
 */
public class ToolbarSetItemEvent extends AbstractEvent {

    private int mItemId;
    private boolean mVisible;

    public ToolbarSetItemEvent(final int itemId, final boolean isVisible) {
        mItemId = itemId;
        mVisible = isVisible;
    }

    public int getItemId() {
        return mItemId;
    }

    public boolean isVisible() {
        return mVisible;
    }
}
