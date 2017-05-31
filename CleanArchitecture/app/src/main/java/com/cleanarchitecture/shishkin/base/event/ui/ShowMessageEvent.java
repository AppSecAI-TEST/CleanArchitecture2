package com.cleanarchitecture.shishkin.base.event.ui;

import android.support.design.widget.Snackbar;

import com.cleanarchitecture.shishkin.base.event.AbstractEvent;

/**
 * Событие - выполнить команду "показать текстовое сообщение"
 */
public class ShowMessageEvent extends AbstractEvent {
    private String mMessage;

    private String mAction;
    private int mDuration;

    public ShowMessageEvent(final String message) {
        mMessage = message;
        mDuration = Snackbar.LENGTH_LONG;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getAction() {
        return mAction;
    }

    public ShowMessageEvent setAction(String action) {
        this.mAction = action;
        return this;
    }

    public int getDuration() {
        return mDuration;
    }

    public ShowMessageEvent setDuration(int duration) {
        this.mDuration = duration;
        return this;
    }


}
