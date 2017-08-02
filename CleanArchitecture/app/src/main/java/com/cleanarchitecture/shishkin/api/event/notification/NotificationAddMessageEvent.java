package com.cleanarchitecture.shishkin.api.event.notification;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

/**
 * Событие - добавить сообщение в зону уведомлений
 */
public class NotificationAddMessageEvent extends AbstractEvent {
    private String mMessage;

    public NotificationAddMessageEvent(final String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }

}
