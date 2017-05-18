package com.cleanarchitecture.shishkin.base.event.toolbar;

import android.view.MenuItem;
import com.cleanarchitecture.shishkin.base.event.AbstractEvent;

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
