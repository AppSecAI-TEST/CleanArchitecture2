package com.cleanarchitecture.shishkin.api.controller;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.event.notification.NotificationAddDistinctMessageEvent;
import com.cleanarchitecture.shishkin.api.event.notification.NotificationAddMessageEvent;
import com.cleanarchitecture.shishkin.api.event.notification.NotificationClearEvent;
import com.cleanarchitecture.shishkin.api.event.notification.NotificationDeleteMessagesEvent;
import com.cleanarchitecture.shishkin.api.event.notification.NotificationReplaceMessageEvent;
import com.cleanarchitecture.shishkin.api.event.notification.NotificationSetMessagesCountEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetBadgeEvent;
import com.cleanarchitecture.shishkin.api.storage.CacheUtils;
import com.cleanarchitecture.shishkin.application.ui.activity.MainActivity;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class NotificationModule extends AbstractModule implements IModuleSubscriber {

    public static final String NAME = NotificationModule.class.getName();
    private static final String LOG_TAG = "NotificationModule:";

    private List<String> mMessages;
    private int mMessagesCount = 5;
    private static final String CANAL_ID = "CANAL_" + BuildConfig.APPLICATION_ID;
    private static final String CANAL_NAME = "Notification Service Canal";
    public static final String ACTION_CLICK = NAME + ".ACTION_CLICK";
    public static final String ACTION_DELETE_MESSAGES = NAME + ".ACTION_DELETE_MESSAGES";

    private synchronized void getCache() {
        final String s = (String) CacheUtils.get(NAME, CacheUtils.USE_ONLY_DISK_CACHE);
        if (StringUtils.isNullOrEmpty(s)) {
            mMessages = Collections.synchronizedList(new LinkedList<String>());
        } else {
            mMessages = AdminUtils.getTransformDataModule().fromJson(s, new com.google.gson.reflect.TypeToken<List<String>>() {
            });
            if (mMessages == null) {
                mMessages = Collections.synchronizedList(new LinkedList<String>());
            }
        }
    }

    private synchronized void sendNotification(final String message) {
        final Context context = AdminUtils.getContext();
        if (context == null) {
            return;
        }

        try {
            final StringBuilder sb = new StringBuilder();
            int cnt = mMessagesCount;
            if (mMessagesCount == 0) {
                cnt = mMessages.size();
            }
            if (cnt > mMessages.size()) {
                cnt = mMessages.size();
            }
            for (int i = 0; i < cnt; i++) {
                if (i > 0) {
                    sb.append("\n\n");
                }
                sb.append(mMessages.get(i));
            }

            AdminUtils.showShortcutBadger(mMessages.size());
            AdminUtils.postEvent(new ToolbarSetBadgeEvent(String.valueOf(mMessages.size()), true));

            final NotificationManager nm = AdminUtils.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                if (ApplicationUtils.hasO()) {
                    NotificationChannel mChannel = nm.getNotificationChannel(CANAL_ID);
                    if (mChannel == null) {
                        final int importance = NotificationManager.IMPORTANCE_LOW;
                        mChannel = new NotificationChannel(CANAL_ID, CANAL_NAME, importance);
                        // Configure the notification channel.
                        //mChannel.setDescription(description);
                        mChannel.enableLights(true);
                        // Sets the notification light color for notifications posted to this
                        // channel, if the device supports this feature.
                        mChannel.setLightColor(R.color.red);
                        mChannel.enableVibration(true);
                        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                        nm.createNotificationChannel(mChannel);
                    }
                }

                final Intent intent = new Intent(context, MainActivity.class);
                intent.setAction(ACTION_CLICK);
                final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                final Intent intentDelete = new Intent(context, NotificationBroadcastReceiver.class);
                intent.setAction(ACTION_DELETE_MESSAGES);
                final PendingIntent pendingDeleteIntent = PendingIntent.getBroadcast(context, 0, intentDelete, PendingIntent.FLAG_CANCEL_CURRENT);

                final NotificationCompat.Builder builderCompat = new NotificationCompat.Builder(context, CANAL_ID);
                final Notification notification = builderCompat
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent)
                        .setTicker(context.getString(R.string.app_name))
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(sb.toString()))
                        .setPriority(0)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setContentText(message)
                        .setDeleteIntent(pendingDeleteIntent)
                        .setChannelId(CANAL_ID)
                        .build();

                nm.notify(R.id.notification_service, notification);
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        }
    }

    private synchronized void addMessage(final String message) {
        getCache();
        mMessages.add(0, message);

        CacheUtils.put(NAME, CacheUtils.USE_ONLY_DISK_CACHE, AdminUtils.getTransformDataModule().toJson(mMessages), 0);
        sendNotification(message);
    }

    private synchronized void addDistinctMessage(final String message) {
        getCache();
        if (!mMessages.contains(message)) {
            mMessages.add(0, message);

            CacheUtils.put(NAME, CacheUtils.USE_ONLY_DISK_CACHE, AdminUtils.getTransformDataModule().toJson(mMessages), 0);
            sendNotification(message);
        }
    }

    private synchronized void replaceMessage(final String message) {
        getCache();
        mMessages.clear();
        mMessages.add(0, message);

        CacheUtils.put(NAME, CacheUtils.USE_ONLY_DISK_CACHE, AdminUtils.getTransformDataModule().toJson(mMessages), 0);
        sendNotification(message);
    }

    private synchronized void clear() {
        getCache();
        mMessages.clear();
        CacheUtils.put(NAME, CacheUtils.USE_ONLY_DISK_CACHE, AdminUtils.getTransformDataModule().toJson(mMessages), 0);

        final NotificationManager nm = AdminUtils.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancelAll();
        }

        AdminUtils.hideShortcutBadger();
        AdminUtils.postEvent(new ToolbarSetBadgeEvent(null, false));
    }

    private synchronized void deleteMessages() {
        getCache();
        mMessages.clear();
        CacheUtils.put(NAME, CacheUtils.USE_ONLY_DISK_CACHE, AdminUtils.getTransformDataModule().toJson(mMessages), 0);

        AdminUtils.hideShortcutBadger();
        AdminUtils.postEvent(new ToolbarSetBadgeEvent(null, false));
    }

    private synchronized void setMessagesCount(int count) {
        mMessagesCount = count;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return null;
    }

    @Override
    public String getDescription() {
        final Context context = ApplicationController.getInstance();
        if (context != null) {
            return context.getString(R.string.module_notification);
        }
        return "Notification module";
    }

    @Override
    public List<String> hasSubscriberType() {
        final List<String> list = new ArrayList<>();
        list.add(EventBusController.SUBSCRIBER_TYPE);
        return list;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onNotificationAddMessageEvent(final NotificationAddMessageEvent event) {
        addMessage(event.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onNotificationAddDistinctMessageEvent(final NotificationAddDistinctMessageEvent event) {
        addDistinctMessage(event.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onNotificationReplaceMessageEvent(final NotificationReplaceMessageEvent event) {
        replaceMessage(event.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onNotificationClearEvent(final NotificationClearEvent event) {
        clear();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onNotificationSetMessagesCountEvent(final NotificationSetMessagesCountEvent event) {
        setMessagesCount(event.getCount());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onNotificationDeleteMessagesEvent(final NotificationDeleteMessagesEvent event) {
        deleteMessages();
    }
}
