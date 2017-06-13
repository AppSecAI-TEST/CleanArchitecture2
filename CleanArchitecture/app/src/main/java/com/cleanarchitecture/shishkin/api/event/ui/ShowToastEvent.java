package com.cleanarchitecture.shishkin.api.event.ui;

import android.widget.Toast;

import com.cleanarchitecture.shishkin.api.controller.ActivityController;
import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

/**
 * Событие - выполнить команду "показать Toast"
 */
public class ShowToastEvent extends AbstractEvent {
    private String mMessage;
    private int mDuration = Toast.LENGTH_LONG;
    private int mType = ActivityController.TOAST_TYPE_INFO;

    public ShowToastEvent(final String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }

    public int getDuration() {
        return mDuration;
    }

    public ShowToastEvent setDuration(int duration) {
        this.mDuration = duration;
        return this;
    }

    public int getType() {
        return mType;
    }

    public ShowToastEvent setType(int type) {
        this.mType = type;
        return this;
    }

}
