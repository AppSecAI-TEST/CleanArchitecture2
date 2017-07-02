package com.cleanarchitecture.shishkin.api.event.ui;

import android.support.design.widget.Snackbar;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

import static com.cleanarchitecture.shishkin.api.controller.ActivityController.TOAST_TYPE_INFO;

/**
 * Событие - выполнить команду "показать текстовое сообщение"
 */
public class ShowMessageEvent extends AbstractEvent {
    private String mMessage;

    private String mAction;
    private int mDuration = Snackbar.LENGTH_LONG;
    private int mType = TOAST_TYPE_INFO;

    public ShowMessageEvent(final String message) {
        mMessage = message;
    }

    public ShowMessageEvent(final String message, final int type) {
        mMessage = message;
        mType = type;
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

    public int getType() {
        return mType;
    }

    public ShowMessageEvent setType(int type) {
        this.mType = type;
        return this;
    }

}
