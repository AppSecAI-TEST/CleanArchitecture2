package com.cleanarchitecture.shishkin.base.event.toolbar;

import com.cleanarchitecture.shishkin.base.event.AbstractEvent;

/**
 * Событие - установить меню Toolbar
 */
public class ToolbarSetMenuEvent extends AbstractEvent {

    private int mMenuId;
    private boolean mVisible;

    public ToolbarSetMenuEvent(final int menuId, final boolean isVisible) {
        mMenuId = menuId;
        mVisible = isVisible;
    }

    public int getMenuId() {
        return mMenuId;
    }

    public boolean isVisible() {
        return mVisible;
    }
}
