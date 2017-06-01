package com.cleanarchitecture.shishkin.base.event.toolbar;

import android.graphics.drawable.Drawable;

import com.cleanarchitecture.shishkin.base.event.AbstractEvent;

/**
 * Событие - установить фон Toolbar
 */
public class ToolbarSetBackgroundEvent extends AbstractEvent {

    private Drawable mDrawable;

    public ToolbarSetBackgroundEvent(final Drawable drawable) {
        mDrawable = drawable;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

}
