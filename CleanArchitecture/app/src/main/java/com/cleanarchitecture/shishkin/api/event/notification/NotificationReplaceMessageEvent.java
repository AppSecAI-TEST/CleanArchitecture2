package com.cleanarchitecture.shishkin.api.event.notification;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

/**
 * Событие - заменить сообщение в зоне уведомлений
 */
public class NotificationReplaceMessageEvent extends AbstractEvent {
    private String mMessage;

    public NotificationReplaceMessageEvent(final String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }

}
