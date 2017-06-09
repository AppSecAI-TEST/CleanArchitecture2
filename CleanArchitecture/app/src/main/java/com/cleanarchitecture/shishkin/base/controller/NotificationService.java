package com.cleanarchitecture.shishkin.base.controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.WorkerThread;
import android.support.v4.app.NotificationCompat;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.ui.activity.MainActivity;
import com.cleanarchitecture.shishkin.base.storage.DiskStorage;
import com.cleanarchitecture.shishkin.base.utils.AdminUtils;
import com.cleanarchitecture.shishkin.base.utils.IntentUtils;
import com.cleanarchitecture.shishkin.base.utils.SerializableUtil;
import com.cleanarchitecture.shishkin.base.utils.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Сервис вывода сообщений в зону уведомлений
 */
@SuppressWarnings("unused")
public class NotificationService extends LiveLongBackgroundIntentService {

    private static final String NAME = "NotificationService";

    public static final String ACTION_CLICK = BuildConfig.APPLICATION_ID + ".NotificationService.ACTION_CLICK";
    public static final String ACTION_DELETE_MESSAGES = BuildConfig.APPLICATION_ID + "action.NotificationBroadcastReceiver.ACTION_DELETE_MESSAGES";
    private static final String ACTION_ADD_MESSAGE = BuildConfig.APPLICATION_ID + ".NotificationService.ACTION_ADD_MESSAGE";
    private static final String ACTION_ADD_DISTINCT_MESSAGE = BuildConfig.APPLICATION_ID + ".NotificationService.ACTION_ADD_DISTINCT_MESSAGE";
    private static final String ACTION_REPLACE_MESSAGE = BuildConfig.APPLICATION_ID + ".NotificationService.ACTION_REPLACE_MESSAGE";
    private static final String ACTION_REFRESH = BuildConfig.APPLICATION_ID + ".NotificationService.ACTION_REFRESH";
    private static final String ACTION_CLEAR = BuildConfig.APPLICATION_ID + ".NotificationService.ACTION_CLEAR";
    private static final String ACTION_SET_MESSAGES_COUNT = BuildConfig.APPLICATION_ID + ".NotificationService.ACTION_SET_MESSAGES_COUNT";
    private List<String> mMessages;
    private int mMessagesCount = 5;

    private static final TimeUnit TIMEUNIT = TimeUnit.MINUTES;
    private static final long TIMEUNIT_DURATION = 5L;

    public NotificationService() {
        super(NAME);

        mMessages = Collections.synchronizedList(new LinkedList<String>());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setShutdownTimeout(TIMEUNIT.toMillis(TIMEUNIT_DURATION));
    }


    /**
     * Добавить сообщщение.
     *
     * @param text текст сообщения
     */
    public static synchronized void addMessage(final Context context, final String text) {
        if (context != null) {
            final Intent intent = IntentUtils.createActionIntent(context, NotificationService.class,
                    ACTION_ADD_MESSAGE);
            intent.putExtra(Intent.EXTRA_TEXT, text);
            context.startService(intent);
        }
    }

    /**
     * Добавить сообщение, если его нет в списке сообщений
     *
     * @param text текст сообщения
     */
    public static synchronized void addDistinctMessage(final Context context, final String text) {
        if (context != null) {
            final Intent intent = IntentUtils.createActionIntent(context, NotificationService.class,
                    ACTION_ADD_DISTINCT_MESSAGE);
            intent.putExtra(Intent.EXTRA_TEXT, text);
            context.startService(intent);
        }
    }

    /**
     * Заменить сообщение
     *
     * @param text текст сообщения
     */
    public static synchronized void replaceMessage(final Context context, final String text) {
        if (context != null) {
            final Intent intent = IntentUtils.createActionIntent(context, NotificationService.class,
                    ACTION_REPLACE_MESSAGE);
            intent.putExtra(Intent.EXTRA_TEXT, text);
            context.startService(intent);
        }
    }

    /**
     * Обновить зону уведомлений
     */
    public static synchronized void refresh(final Context context) {
        if (context != null) {
            final Intent intent = IntentUtils.createActionIntent(context, NotificationService.class,
                    ACTION_REFRESH);
            context.startService(intent);
        }
    }

    /**
     * Очистить зону уведомлений
     */
    public static synchronized void clear(final Context context) {
        if (context != null) {
            final Intent intent = IntentUtils.createActionIntent(context, NotificationService.class,
                    ACTION_CLEAR);
            context.startService(intent);
        }
    }

    /**
     * Очистить список сообщений
     */
    public static synchronized void clearMessages(final Context context) {
        if (context != null) {
            final Intent intent = IntentUtils.createActionIntent(context, NotificationService.class,
                    ACTION_DELETE_MESSAGES);
            context.startService(intent);
        }
    }

    /**
     * установить максимальное кол-во сообщений
     */
    public static synchronized void setMessagesCount(final Context context, final int count) {
        if (context != null) {
            final Intent intent = IntentUtils.createActionIntent(context, NotificationService.class,
                    ACTION_SET_MESSAGES_COUNT);
            intent.putExtra(Intent.EXTRA_TEXT, String.valueOf(count));
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

        if (ACTION_ADD_MESSAGE.equals(action)) {
            final String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            onHandleAddMessageAction(text);

        } else if (ACTION_ADD_DISTINCT_MESSAGE.equals(action)) {
            final String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            onHandleAddDistinctMessageAction(text);

        } else if (ACTION_REPLACE_MESSAGE.equals(action)) {
            final String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            onHandleReplaceMessageAction(text);

        } else if (ACTION_REFRESH.equals(action)) {
            onHandleRefreshAction();

        } else if (ACTION_CLEAR.equals(action)) {
            onHandleClearAction();

        } else if (ACTION_DELETE_MESSAGES.equals(action)) {
            onHandleDeleteMessagesAction();

        } else if (ACTION_SET_MESSAGES_COUNT.equals(action)) {
            final int count = StringUtils.toInt(intent.getStringExtra(Intent.EXTRA_TEXT));
            onHandleSetMessagesCount(count);
        }
    }

    private synchronized void sendNotification(final String message) {
        try {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mMessages.size(); i++) {
                sb.append(mMessages.get(i));
                sb.append("\n\n");
            }

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
                        .build();

                nm.notify(R.id.notification_service, notification);
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(NAME, e);
        }
    }

    @WorkerThread
    private void onHandleAddMessageAction(final String message) {
        final List<String> list = SerializableUtil.serializableToList(DiskStorage.getInstance(getApplicationContext()).get(NAME));
        if (list != null) {
            mMessages = list;
        }

        mMessages.add(0, message);
        while (mMessages.size() > mMessagesCount) {
            mMessages.remove(mMessages.get(mMessages.size() - 1));
        }
        DiskStorage.getInstance(getApplicationContext()).put(NAME, SerializableUtil.toSerializable(mMessages));

        sendNotification(message);
    }

    @WorkerThread
    private void onHandleAddDistinctMessageAction(final String message) {
        final List<String> list = SerializableUtil.serializableToList(DiskStorage.getInstance(getApplicationContext()).get(NAME));
        if (list != null) {
            mMessages = list;
        }

        if (!mMessages.contains(message)) {
            mMessages.add(0, message);
            while (mMessages.size() > mMessagesCount) {
                mMessages.remove(mMessages.get(mMessages.size() - 1));
            }
            DiskStorage.getInstance(getApplicationContext()).put(NAME, SerializableUtil.toSerializable(mMessages));

            sendNotification(message);
        } else {
            onHandleRefreshAction();
        }
    }

    @WorkerThread
    private void onHandleReplaceMessageAction(final String message) {

        mMessages.clear();
        mMessages.add(0, message);
        DiskStorage.getInstance(getApplicationContext()).put(NAME, SerializableUtil.toSerializable(mMessages));

        sendNotification(message);
    }

    @WorkerThread
    private void onHandleRefreshAction() {
        final List<String> list = SerializableUtil.serializableToList(DiskStorage.getInstance(getApplicationContext()).get(NAME));
        if (list != null) {
            mMessages = list;
        }

        if (mMessages.isEmpty()) {
            onHandleClearAction();
            return;
        }

        final String message = mMessages.get(0);

        sendNotification(message);
    }

    @WorkerThread
    private void onHandleClearAction() {
        mMessages.clear();
        DiskStorage.getInstance(getApplicationContext()).put(NAME, SerializableUtil.toSerializable(mMessages));

        final NotificationManager nm = AdminUtils.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancelAll();
        }
    }

    @WorkerThread
    private void onHandleDeleteMessagesAction() {
        mMessages.clear();
        DiskStorage.getInstance(getApplicationContext()).put(NAME, SerializableUtil.toSerializable(mMessages));
    }

    @WorkerThread
    private void onHandleSetMessagesCount(final int count) {
        mMessagesCount = count;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
