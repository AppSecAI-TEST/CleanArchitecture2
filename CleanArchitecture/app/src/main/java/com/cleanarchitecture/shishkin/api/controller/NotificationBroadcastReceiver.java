package com.cleanarchitecture.shishkin.api.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cleanarchitecture.shishkin.api.event.notification.NotificationDeleteMessagesEvent;

/**
 * BroadcastReceiver принимающий сообщение при свайпе на сообщении в зоне уведомлений
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            AdminUtils.postEvent(new NotificationDeleteMessagesEvent());
        }
    }
}
