package com.cleanarchitecture.shishkin.api.event.ui;


import android.view.Gravity;
import android.view.View;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

/**
 * Событие - выполнить команду "показать Tooltip"
 */
public class ShowTooltipEvent extends AbstractEvent {

    private View mView;
    private int mResId = 0;
    private int mGravity = Gravity.BOTTOM | Gravity.END;

    public ShowTooltipEvent(final View anchorView, final int resId, final int gravity) {
        mView = anchorView;
        mResId = resId;
        mGravity = gravity;
    }

    public View getView() {
        return mView;
    }

    public int getResId() {
        return mResId;
    }

    public int getGravity() {
        return mGravity;
    }

}
