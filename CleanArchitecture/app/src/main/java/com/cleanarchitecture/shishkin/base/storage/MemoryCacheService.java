package com.cleanarchitecture.shishkin.base.storage;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.WorkerThread;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.cleanarchitecture.shishkin.base.controller.LiveLongBackgroundIntentService;
import com.cleanarchitecture.shishkin.base.utils.IntentUtils;
import com.github.snowdream.android.util.Log;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class MemoryCacheService extends LiveLongBackgroundIntentService {

    private static final String LOG_TAG = "MemoryCacheService:";
    private static final String NAME = "MemoryCacheService";

    private static final String EXTRA_SERIALIZABLE = "EXTRA_SERIALIZABLE";
    private static final String ACTION_PUT = BuildConfig.APPLICATION_ID + ".MemoryCacheService.PUT";
    private static final String ACTION_CLEAR = BuildConfig.APPLICATION_ID + ".MemoryCacheService.CLEAR";
    private static final String ACTION_CLEAR_ALL = BuildConfig.APPLICATION_ID + ".MemoryCacheService.CLEAR_ALL";

    private static final TimeUnit TIMEUNIT = TimeUnit.MINUTES;
    private static final long TIMEUNIT_DURATION = 5L;

    public MemoryCacheService() {
        super(NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setShutdownTimeout(TIMEUNIT.toMillis(TIMEUNIT_DURATION));
    }

    public synchronized static void put(final Context context, final String key, final Serializable object) {
        if (context != null) {
            final Intent intent = IntentUtils.createActionIntent(context, MemoryCacheService.class,
                    ACTION_PUT);
            intent.putExtra(Intent.EXTRA_TEXT, key);
            intent.putExtra(EXTRA_SERIALIZABLE, object);
            context.startService(intent);
        }
    }

    public synchronized static void clear(final Context context, final String key) {
        if (context != null) {
            final Intent intent = IntentUtils.createActionIntent(context, MemoryCacheService.class,
                    ACTION_CLEAR);
            intent.putExtra(Intent.EXTRA_TEXT, key);
            context.startService(intent);
        }
    }

    public synchronized static void clearAll(final Context context) {
        if (context != null) {
            final Intent intent = IntentUtils.createActionIntent(context, MemoryCacheService.class,
                    ACTION_CLEAR_ALL);
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

        if (ACTION_PUT.equals(action)) {
            final String key = intent.getStringExtra(Intent.EXTRA_TEXT);
            final Serializable object = intent.getSerializableExtra(EXTRA_SERIALIZABLE);
            onHandlePutAction(key, object);

        } else if (ACTION_CLEAR.equals(action)) {
            final String key = intent.getStringExtra(Intent.EXTRA_TEXT);
            onHandleClearAction(key);

        } else if (ACTION_CLEAR_ALL.equals(action)) {
            onHandleClearAllAction();

        }
    }

    @WorkerThread
    private void onHandlePutAction(final String key, final Serializable object) {
        MemoryCache.getInstance().put(key, object);
    }

    @WorkerThread
    private void onHandleClearAction(final String key) {
        MemoryCache.getInstance().clear(key);
    }

    @WorkerThread
    private void onHandleClearAllAction() {
        MemoryCache.getInstance().clearAll();
    }

}

