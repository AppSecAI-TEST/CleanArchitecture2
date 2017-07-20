package com.cleanarchitecture.shishkin.api.controller;

import android.content.Context;
import android.content.Intent;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.cleanarchitecture.shishkin.api.service.NotificationService;
import com.cleanarchitecture.shishkin.common.utils.IntentUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationModule extends AbstractModule implements INotificationModule {

    public static final String NAME = NotificationModule.class.getName();

    public static final String ACTION_ADD_MESSAGE = BuildConfig.APPLICATION_ID + ".ACTION_ADD_MESSAGE";
    public static final String ACTION_ADD_DISTINCT_MESSAGE = BuildConfig.APPLICATION_ID + ".ACTION_ADD_DISTINCT_MESSAGE";
    public static final String ACTION_REPLACE_MESSAGE = BuildConfig.APPLICATION_ID + ".ACTION_REPLACE_MESSAGE";
    public static final String ACTION_REFRESH_MESSAGES = BuildConfig.APPLICATION_ID + ".ACTION_REFRESH_MESSAGES";
    public static final String ACTION_CLEAR_MESSAGES = BuildConfig.APPLICATION_ID + ".ACTION_CLEAR_MESSAGES";
    public static final String ACTION_SET_MESSAGES_COUNT = BuildConfig.APPLICATION_ID + ".ACTION_SET_MESSAGES_COUNT";

    private Map<String, Class> mServices = Collections.synchronizedMap(new ConcurrentHashMap<String, Class>());

    public NotificationModule() {
        mServices.put(NotificationService.NAME, NotificationService.class);
    }

    private synchronized void sendIntent(final String action, final String message) {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            for (Class clss : mServices.values()) {
                final Intent intent = IntentUtils.createActionIntent(context, clss, action);
                intent.putExtra(Intent.EXTRA_TEXT, message);
                context.startService(intent);
            }
        }
    }

    private synchronized void sendIntent(final String action) {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            for (Class clss : mServices.values()) {
                final Intent intent = IntentUtils.createActionIntent(context, clss, action);
                context.startService(intent);
            }
        }
    }

    @Override
    public synchronized void addService(final String name, final Class clss) {
        if (!StringUtils.isNullOrEmpty(name) && clss != null) {
            mServices.put(name, clss);
        }
    }

    @Override
    public synchronized void addMessage(final String message) {
        if (StringUtils.isNullOrEmpty(message)) {
            return;
        }

        sendIntent(ACTION_ADD_MESSAGE, message);
    }

    @Override
    public synchronized void addDistinctMessage(final String message) {
        if (StringUtils.isNullOrEmpty(message)) {
            return;
        }

        sendIntent(ACTION_ADD_DISTINCT_MESSAGE, message);
    }

    @Override
    public synchronized void replaceMessage(final String message) {
        if (StringUtils.isNullOrEmpty(message)) {
            return;
        }

        sendIntent(ACTION_REPLACE_MESSAGE, message);
    }

    @Override
    public synchronized void refresh() {
        sendIntent(ACTION_REFRESH_MESSAGES);
    }

    @Override
    public synchronized void clear() {
        sendIntent(ACTION_CLEAR_MESSAGES);
    }

    @Override
    public synchronized void setMessagesCount(final int count) {
        sendIntent(ACTION_SET_MESSAGES_COUNT);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return null;
    }
}

