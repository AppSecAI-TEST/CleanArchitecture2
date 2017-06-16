package com.cleanarchitecture.shishkin.api.event.ui;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.api.event.AbstractEvent;
import com.cleanarchitecture.shishkin.api.event.IEvent;

/**
 * Событие - выполнить команду "показать сообщение об ошибке"
 */
public class ShowErrorMessageEvent extends AbstractEvent {
    private String mMessage;

    public ShowErrorMessageEvent(final IEvent event) {
        if (getErrorCode() != 0) {
            mMessage = AdminUtils.getErrorText(getErrorCode());
        } else {
            mMessage = event.getErrorText();
        }
    }

    public ShowErrorMessageEvent(final String message) {
        mMessage = message;
    }

    public ShowErrorMessageEvent(final int errorCode) {
        mMessage = AdminUtils.getErrorText(errorCode);
    }

    public String getMessage() {
        return mMessage;
    }

}
