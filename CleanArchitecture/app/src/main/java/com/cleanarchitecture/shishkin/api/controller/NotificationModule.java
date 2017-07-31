package com.cleanarchitecture.shishkin.api.controller;

import android.content.Context;
import android.content.Intent;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.service.BadgeService;
import com.cleanarchitecture.shishkin.api.service.BoardService;
import com.cleanarchitecture.shishkin.api.service.NotificationService;
import com.cleanarchitecture.shishkin.common.utils.IntentUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationModule extends AbstractShortlyLiveModule implements INotificationModule {

    public static final String NAME = NotificationModule.class.getName();

    public static final String ACTION_ADD_MESSAGE = BuildConfig.APPLICATION_ID + ".ACTION_ADD_MESSAGE";
    public static final String ACTION_ADD_DISTINCT_MESSAGE = BuildConfig.APPLICATION_ID + ".ACTION_ADD_DISTINCT_MESSAGE";
    public static final String ACTION_REPLACE_MESSAGE = BuildConfig.APPLICATION_ID + ".ACTION_REPLACE_MESSAGE";
    public static final String ACTION_REFRESH_MESSAGES = BuildConfig.APPLICATION_ID + ".ACTION_REFRESH_MESSAGES";
    public static final String ACTION_CLEAR_MESSAGES = BuildConfig.APPLICATION_ID + ".ACTION_CLEAR_MESSAGES";
    public static final String ACTION_SET_MESSAGES_COUNT = BuildConfig.APPLICATION_ID + ".ACTION_SET_MESSAGES_COUNT";

    private Map<String, Class> mServices = Collections.synchronizedMap(new ConcurrentHashMap<String, Class>());

    public NotificationModule() {
        final IPreferencesModule module = PreferencesModule.getInstance();
        if (module != null) {
            if (module.getModule(NotificationService.NAME)) {
                mServices.put(NotificationService.NAME, NotificationService.class);
            }
            if (module.getModule(BoardService.NAME)) {
                mServices.put(BoardService.NAME, BoardService.class);
            }
            if (module.getModule(BadgeService.NAME)) {
                mServices.put(BadgeService.NAME, BadgeService.class);
            }
        }
    }

    @Override
    public void onUnRegister() {
        clearAll();
    }

    private synchronized void sendIntent(final String name, final String action, final String message) {
        post();

        if (mServices.containsKey(name)) {
            final Context context = AdminUtils.getContext();
            if (context != null) {
                final Class clss = mServices.get(name);
                if (clss != null) {
                    final Intent intent = IntentUtils.createActionIntent(context, clss, action);
                    intent.putExtra(Intent.EXTRA_TEXT, message);
                    context.startService(intent);
                }
            }
        }
    }

    private synchronized void sendIntentAll(final String action) {
        post();

        final Context context = AdminUtils.getContext();
        if (context != null) {
            for (Class clss : mServices.values()) {
                final Intent intent = IntentUtils.createActionIntent(context, clss, action);
                context.startService(intent);
            }
        }
    }

    private synchronized void sendIntent(final String name, final String action) {
        post();

        if (mServices.containsKey(name)) {
            final Context context = AdminUtils.getContext();
            if (context != null) {
                final Class clss = mServices.get(name);
                if (clss != null) {
                    final Intent intent = IntentUtils.createActionIntent(context, clss, action);
                    context.startService(intent);
                }
            }
        }
    }

    @Override
    public synchronized void addService(final String name, final Class clss) {
        post();

        if (!StringUtils.isNullOrEmpty(name) && clss != null) {
            mServices.put(name, clss);
        }
    }

    @Override
    public synchronized void addMessage(final String name, final String message) {
        if (StringUtils.isNullOrEmpty(message)) {
            return;
        }

        sendIntent(name, ACTION_ADD_MESSAGE, message);
    }

    @Override
    public synchronized void addDistinctMessage(final String name, final String message) {
        if (StringUtils.isNullOrEmpty(message)) {
            return;
        }

        sendIntent(name, ACTION_ADD_DISTINCT_MESSAGE, message);
    }

    @Override
    public synchronized void replaceMessage(final String name, final String message) {
        if (StringUtils.isNullOrEmpty(message)) {
            return;
        }

        sendIntent(name, ACTION_REPLACE_MESSAGE, message);
    }

    @Override
    public synchronized void refresh(final String name) {
        sendIntent(name, ACTION_REFRESH_MESSAGES);
    }

    @Override
    public synchronized void clearAll() {
        sendIntentAll(ACTION_CLEAR_MESSAGES);
    }

    @Override
    public synchronized void clear(final String name) {
        sendIntent(name, ACTION_CLEAR_MESSAGES);
    }

    @Override
    public synchronized void setMessagesCount(final String name, final int count) {
        post();

        final Context context = AdminUtils.getContext();
        if (context != null) {
            final Class clss = mServices.get(name);
            if (clss != null) {
                final Intent intent = IntentUtils.createActionIntent(context, clss, ACTION_SET_MESSAGES_COUNT);
                intent.putExtra(Intent.EXTRA_TEXT, String.valueOf(count));
                context.startService(intent);
            }
        }
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


}

