package com.cleanarchitecture.shishkin.base.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            NotificationService.clearMessages(context);
        }
    }

}
