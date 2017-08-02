package com.cleanarchitecture.shishkin.api.event.notification;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

/**
 * Событие - добавить сообщение в зону уведомлений
 */
public class NotificationSetMessagesCountEvent extends AbstractEvent {
    private int mCount;

    public NotificationSetMessagesCountEvent(final int count) {
        mCount = count;
    }

    public int getCount() {
        return mCount;
    }

}
