package com.cleanarchitecture.shishkin.base.event.ui;

import android.widget.Toast;

import com.cleanarchitecture.shishkin.base.event.AbstractEvent;
import com.cleanarchitecture.shishkin.base.presenter.ActivityPresenter;

/**
 * Событие - выполнить команду "показать Toast"
 */
public class ShowToastEvent extends AbstractEvent {
    private String mMessage;
    private int mDuration = Toast.LENGTH_LONG;
    private int mType = ActivityPresenter.TOAST_TYPE_INFO;

    public ShowToastEvent(final String message) {
        mMessage = message;
    }

    public ShowToastEvent(final String message, final int duration) {
        mMessage = message;
        mDuration = duration;
    }

    public ShowToastEvent(final String message, final int duration, final int type) {
        mMessage = message;
        mDuration = duration;
        mType = type;
    }

    public String getMessage() {
        return mMessage;
    }

    public int getDuration() {
        return mDuration;
    }

    public int getType() {
        return mType;
    }

}
