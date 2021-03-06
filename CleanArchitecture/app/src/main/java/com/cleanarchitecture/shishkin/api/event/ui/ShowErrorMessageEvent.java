package com.cleanarchitecture.shishkin.api.event.ui;

import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.event.AbstractEvent;
import com.cleanarchitecture.shishkin.api.event.IEvent;

/**
 * Событие - выполнить команду "показать сообщение об ошибке"
 */
public class ShowErrorMessageEvent extends AbstractEvent {
    private String mMessage;

    public ShowErrorMessageEvent(final IEvent event) {
        mMessage = getError().getErrorText();
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
