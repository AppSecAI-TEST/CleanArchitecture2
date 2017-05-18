package com.cleanarchitecture.shishkin.base.event.ui;

import android.os.Bundle;
import android.support.design.widget.Snackbar;

import com.cleanarchitecture.shishkin.base.event.AbstractEvent;

/**
 * Событие - выполнить команду "показать текстовое сообщение"
 */
public class ShowMessageEvent extends AbstractEvent {
    private String mMessage;
    private String mAction;
    private Bundle mBundle;
    private int mDuration;

    public ShowMessageEvent(final String message) {
        mMessage = message;
        mDuration = Snackbar.LENGTH_LONG;
    }

    public ShowMessageEvent(final String message, final int duration, final String action) {
        mMessage = message;
        mDuration = duration;
        mAction = action;
    }

    public ShowMessageEvent(final String message, final int duration, final String action, final Bundle bundle) {
        this(message, duration, action);
        mBundle = bundle;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getAction() {
        return mAction;
    }

    public Bundle getBundle() {
        return mBundle;
    }

    public int getDuration() {
        return mDuration;
    }
}
