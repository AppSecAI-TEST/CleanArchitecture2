package com.cleanarchitecture.shishkin.base.event.toolbar;

import com.cleanarchitecture.shishkin.base.event.AbstractEvent;

/**
 * Событие - установить флаг BackNavigation Toolbar
 */
public class ToolbarSetBackNavigationEvent extends AbstractEvent {

    private boolean mBackNavigation;

    public ToolbarSetBackNavigationEvent(boolean backNavigation) {
        mBackNavigation = backNavigation;
    }

    public boolean getBackNavigation() {
        return mBackNavigation;
    }
}
