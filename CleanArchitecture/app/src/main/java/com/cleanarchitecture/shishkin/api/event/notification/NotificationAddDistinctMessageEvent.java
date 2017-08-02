package com.cleanarchitecture.shishkin.api.event.notification;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

/**
 * Событие - добавить distinct сообщение в зону уведомлений
 */
public class NotificationAddDistinctMessageEvent extends AbstractEvent {
    private String mMessage;

    public NotificationAddDistinctMessageEvent(final String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }

}
