package com.cleanarchitecture.shishkin.api.service;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.NotificationModule;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetBadgeEvent;
import com.cleanarchitecture.shishkin.api.storage.CacheUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * Сервис вывода сообщений в зону BadgeView и Application Badge
 */
@SuppressWarnings("unused")
public class BadgeService extends ShortlyLiveBackgroundIntentService {

    public static final String NAME = BadgeService.class.getName();

    private String mMessage;
    private static final TimeUnit TIMEUNIT = TimeUnit.MINUTES;
    private static final long TIMEUNIT_DURATION = 5L;

    public BadgeService() {
        super(NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setShutdownTimeout(TIMEUNIT.toMillis(TIMEUNIT_DURATION));
    }

    @Override
    @WorkerThread
    public void onHandleIntent(@Nullable final Intent intent) {
        if (intent == null) {
            return;
        }

        final String action = intent.getAction();

        if (NotificationModule.ACTION_ADD_MESSAGE.equals(action)) {
            final String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            onHandleAddMessageAction(text);

        } else if (NotificationModule.ACTION_ADD_DISTINCT_MESSAGE.equals(action)) {
            final String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            onHandleAddMessageAction(text);

        } else if (NotificationModule.ACTION_REPLACE_MESSAGE.equals(action)) {
            final String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            onHandleAddMessageAction(text);

        } else if (NotificationModule.ACTION_REFRESH_MESSAGES.equals(action)) {
            onHandleRefreshAction();

        } else if (NotificationModule.ACTION_CLEAR_MESSAGES.equals(action)) {
            onHandleClearAction();

        }
    }

    private void getCache() {
        mMessage = (String) CacheUtils.get(NAME, CacheUtils.USE_ONLY_DISK_CACHE);
    }


    private synchronized void sendNotification() {
        if (StringUtils.isNullOrEmpty(mMessage)) {
            AdminUtils.hideShortcutBadger();
            AdminUtils.postEvent(new ToolbarSetBadgeEvent(null, false));
        } else {
            AdminUtils.showShortcutBadger(StringUtils.toInt(StringUtils.getDigits(mMessage)));
            AdminUtils.postEvent(new ToolbarSetBadgeEvent(mMessage, true));
        }
    }

    @WorkerThread
    private void onHandleAddMessageAction(final String message) {
        getCache();

        mMessage = message;

        CacheUtils.put(NAME, CacheUtils.USE_ONLY_DISK_CACHE, mMessage, 0);
        sendNotification();
    }

    @WorkerThread
    private void onHandleRefreshAction() {
        getCache();

        sendNotification();
    }

    @WorkerThread
    private void onHandleClearAction() {
        getCache();

        mMessage = null;

        CacheUtils.put(NAME, CacheUtils.USE_ONLY_DISK_CACHE, mMessage, 0);
        sendNotification();
    }

    @Override
    public String getName() {
        return NAME;
    }
}
