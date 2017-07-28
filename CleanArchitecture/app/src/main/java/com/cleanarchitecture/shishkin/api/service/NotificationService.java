package com.cleanarchitecture.shishkin.api.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.WorkerThread;
import android.support.v4.app.NotificationCompat;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.api.controller.INotificationModule;
import com.cleanarchitecture.shishkin.api.controller.NotificationModule;
import com.cleanarchitecture.shishkin.api.storage.CacheUtils;
import com.cleanarchitecture.shishkin.application.ui.activity.MainActivity;
import com.cleanarchitecture.shishkin.common.utils.IntentUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Сервис вывода сообщений в зону уведомлений
 */
@SuppressWarnings("unused")
public class NotificationService extends ShortlyLiveBackgroundIntentService {

    public static final String NAME = NotificationService.class.getName();

    private static final String LOG_TAG = "NotificationService:";
    private static final String CANAL_ID = "CANAL_" + BuildConfig.APPLICATION_ID;
    private static final String CANAL_NAME = "Notification Service Canal";
    public static final String ACTION_CLICK = BuildConfig.APPLICATION_ID + ".NotificationService.ACTION_CLICK";
    public static final String ACTION_DELETE_MESSAGES = BuildConfig.APPLICATION_ID + "action.NotificationBroadcastReceiver.ACTION_DELETE_MESSAGES";

    private List<String> mMessages;
    private int mMessagesCount = 5;
    private static final TimeUnit TIMEUNIT = TimeUnit.MINUTES;
    private static final long TIMEUNIT_DURATION = 5L;

    public NotificationService() {
        super(NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setShutdownTimeout(TIMEUNIT.toMillis(TIMEUNIT_DURATION));
    }

    /**
     * Очистить список сообщений в зоне уведомлениц
     */
    public static synchronized void clearMessages(final Context context) {
        if (context != null) {
            final Intent intent = IntentUtils.createActionIntent(context, NotificationService.class,
                    ACTION_DELETE_MESSAGES);
            context.startService(intent);
        }
    }

    @Override
    @WorkerThread
    public void onHandleIntent(final Intent intent) {
        if (intent == null) {
            return;
        }

        final String action = intent.getAction();

        if (NotificationModule.ACTION_ADD_MESSAGE.equals(action)) {
            final String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            onHandleAddMessageAction(text);

        } else if (NotificationModule.ACTION_ADD_DISTINCT_MESSAGE.equals(action)) {
            final String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            onHandleAddDistinctMessageAction(text);

        } else if (NotificationModule.ACTION_REPLACE_MESSAGE.equals(action)) {
            final String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            onHandleReplaceMessageAction(text);

        } else if (NotificationModule.ACTION_REFRESH_MESSAGES.equals(action)) {
            onHandleRefreshAction();

        } else if (NotificationModule.ACTION_CLEAR_MESSAGES.equals(action)) {
            onHandleClearAction();

        } else if (ACTION_DELETE_MESSAGES.equals(action)) {
            onHandleDeleteMessagesAction();

        } else if (NotificationModule.ACTION_SET_MESSAGES_COUNT.equals(action)) {
            final int count = StringUtils.toInt(intent.getStringExtra(Intent.EXTRA_TEXT));
            onHandleSetMessagesCount(count);
        }
    }

    private synchronized void sendNotification(final String message) {
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

            final INotificationModule module = AdminUtils.getNotificationModule();
            if (module != null) {
                module.replaceMessage(BadgeService.NAME, String.valueOf(mMessages.size()));
            }

            /*
            if (ApplicationUtils.hasO()) {
                final NotificationManager nm = AdminUtils.getSystemService(Context.NOTIFICATION_SERVICE);
                if (nm != null) {
                    int importance = NotificationManager.IMPORTANCE_LOW;
                    NotificationChannel mChannel = new NotificationChannel(CANAL_ID, CANAL_NAME, importance);
                    // Configure the notification channel.
                    //mChannel.setDescription(description);
                    mChannel.enableLights(true);
                    // Sets the notification light color for notifications posted to this
                    // channel, if the device supports this feature.
                    mChannel.setLightColor(Color.RED);
                    mChannel.enableVibration(true);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    nm.createNotificationChannel(mChannel);
                }
            }
            */

            final Intent intent = new Intent(this, MainActivity.class);
            intent.setAction(ACTION_CLICK);
            final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            final Intent intentDelete = new Intent(this, NotificationBroadcastReceiver.class);
            intent.setAction(ACTION_DELETE_MESSAGES);
            final PendingIntent pendingDeleteIntent = PendingIntent.getBroadcast(this, 0, intentDelete, PendingIntent.FLAG_CANCEL_CURRENT);

            final NotificationManager nm = AdminUtils.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                final NotificationCompat.Builder builderCompat = new NotificationCompat.Builder(getApplicationContext());
                final Notification notification = builderCompat
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent)
                        .setTicker(getApplicationContext().getString(R.string.app_name))
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setContentTitle(getApplicationContext().getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(sb.toString()))
                        .setPriority(0)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setContentText(message)
                        .setDeleteIntent(pendingDeleteIntent)
                        //.setChannelId(CANAL_ID)
                        .build();

                nm.notify(R.id.notification_service, notification);
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        }
    }

    private void getCache() {
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

    @WorkerThread
    private void onHandleAddMessageAction(final String message) {
        getCache();
        mMessages.add(0, message);

        CacheUtils.put(NAME, CacheUtils.USE_ONLY_DISK_CACHE, AdminUtils.getTransformDataModule().toJson(mMessages), 0);
        sendNotification(message);
    }

    @WorkerThread
    private void onHandleAddDistinctMessageAction(final String message) {
        getCache();
        if (!mMessages.contains(message)) {
            mMessages.add(0, message);

            CacheUtils.put(NAME, CacheUtils.USE_ONLY_DISK_CACHE, AdminUtils.getTransformDataModule().toJson(mMessages), 0);
            sendNotification(message);
        } else {
            onHandleRefreshAction();
        }
    }

    @WorkerThread
    private void onHandleReplaceMessageAction(final String message) {
        getCache();
        mMessages.clear();
        mMessages.add(0, message);

        CacheUtils.put(NAME, CacheUtils.USE_ONLY_DISK_CACHE, AdminUtils.getTransformDataModule().toJson(mMessages), 0);
        sendNotification(message);
    }

    @WorkerThread
    private void onHandleRefreshAction() {
        getCache();
        if (mMessages.isEmpty()) {
            onHandleClearAction();
            return;
        }

        final String message = mMessages.get(0);

        sendNotification(message);
    }

    @WorkerThread
    private void onHandleClearAction() {
        getCache();
        mMessages.clear();
        CacheUtils.put(NAME, CacheUtils.USE_ONLY_DISK_CACHE, AdminUtils.getTransformDataModule().toJson(mMessages), 0);

        final NotificationManager nm = AdminUtils.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancelAll();
        }
    }

    @WorkerThread
    private void onHandleDeleteMessagesAction() {
        getCache();
        mMessages.clear();
        CacheUtils.put(NAME, CacheUtils.USE_ONLY_DISK_CACHE, AdminUtils.getTransformDataModule().toJson(mMessages), 0);

        final INotificationModule module = AdminUtils.getNotificationModule();
        if (module != null) {
            module.clear(BadgeService.NAME);
        }
    }

    @WorkerThread
    private void onHandleSetMessagesCount(final int count) {
        if (count >= 0) {
            mMessagesCount = count;
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
