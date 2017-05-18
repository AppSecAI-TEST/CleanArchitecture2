package com.cleanarchitecture.shishkin.base.event.toolbar;

import com.cleanarchitecture.shishkin.base.event.AbstractEvent;

/**
 * Событие - установить заголовок Toolbar
 */
public class ToolbarSetTitleEvent extends AbstractEvent {

    private int mIconId;
    private String mTitle;

    public ToolbarSetTitleEvent(final int iconId, final String title) {
        mIconId = iconId;
        mTitle = title;
    }

    public int getIconId() {
        return mIconId;
    }

    public String getTitle() {
        return mTitle;
    }
}
