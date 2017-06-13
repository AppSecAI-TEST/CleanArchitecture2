package com.cleanarchitecture.shishkin.api.event.toolbar;

import android.view.MenuItem;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

/**
 * Событие - click на меню в Toolbar
 */
public class OnToolbarMenuItemClickEvent extends AbstractEvent {

    private MenuItem mItem;

    public OnToolbarMenuItemClickEvent(final MenuItem item) {
        mItem = item;
    }

    public MenuItem getMenuItem() {
        return mItem;
    }

}
