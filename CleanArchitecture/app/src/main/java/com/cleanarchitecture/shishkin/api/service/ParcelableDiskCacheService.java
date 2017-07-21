package com.cleanarchitecture.shishkin.api.service;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.WorkerThread;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.cleanarchitecture.shishkin.api.controller.Admin;
import com.cleanarchitecture.shishkin.api.storage.IExpiredParcelableStorage;
import com.cleanarchitecture.shishkin.api.storage.ParcelableDiskCache;
import com.cleanarchitecture.shishkin.common.utils.IntentUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class ParcelableDiskCacheService extends ShortlyLiveBackgroundIntentService {

    private static final String NAME = ParcelableDiskCacheService.class.getName();

    private static final String EXTRA_OBJECT = "EXTRA_OBJECT";
    private static final String EXTRA_EXPIRED = "EXTRA_EXPIRED";
    private static final String ACTION_PUT = BuildConfig.APPLICATION_ID + ".ParcelableDiskCacheService.PUT";
    private static final String ACTION_PUT_LIST = BuildConfig.APPLICATION_ID + ".ParcelableDiskCacheService.PUT_LIST";
    private static final String ACTION_PUT_EXPIRED = BuildConfig.APPLICATION_ID + ".ParcelableDiskCacheService.PUT_EXPIRED";
    private static final String ACTION_PUT_LIST_EXPIRED = BuildConfig.APPLICATION_ID + ".ParcelableDiskCacheService.PUT_LIST_EXPIRED";
    private static final String ACTION_CLEAR = BuildConfig.APPLICATION_ID + ".ParcelableDiskCacheService.CLEAR";
    private static final String ACTION_CLEAR_ALL = BuildConfig.APPLICATION_ID + ".ParcelableDiskCacheService.CLEAR_ALL";

    private static final TimeUnit TIMEUNIT = TimeUnit.MINUTES;
    private static final long TIMEUNIT_DURATION = 5L;

    public ParcelableDiskCacheService() {
        super(NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setShutdownTimeout(TIMEUNIT.toMillis(TIMEUNIT_DURATION));
    }

    public static synchronized <T extends Parcelable> void put(final Context context, final String key, final T object) {
        if (context != null && !StringUtils.isNullOrEmpty(key) && object != null) {
            final Intent intent = IntentUtils.createActionIntent(context, ParcelableDiskCacheService.class,
                    ACTION_PUT);
            intent.putExtra(Intent.EXTRA_TEXT, key);
            intent.putExtra(EXTRA_OBJECT, object);
            context.startService(intent);
        }
    }

    public static synchronized <T extends Parcelable> void put(final Context context, final String key, final List<T> objects) {
        if (context != null && !StringUtils.isNullOrEmpty(key) && objects != null) {
            final Intent intent = IntentUtils.createActionIntent(context, ParcelableDiskCacheService.class,
                    ACTION_PUT_LIST);
            intent.putExtra(Intent.EXTRA_TEXT, key);
            final ArrayList<T> list = new ArrayList<>();
            list.addAll(objects);
            intent.putParcelableArrayListExtra(EXTRA_OBJECT, list);
            context.startService(intent);
        }
    }

    public static synchronized void put(final Context context, final String key, final Parcelable object, final long expired) {
        if (context != null && !StringUtils.isNullOrEmpty(key) && object != null) {
            final Intent intent = IntentUtils.createActionIntent(context, ParcelableDiskCacheService.class,
                    ACTION_PUT_EXPIRED);
            intent.putExtra(Intent.EXTRA_TEXT, key);
            intent.putExtra(EXTRA_OBJECT, object);
            intent.putExtra(EXTRA_EXPIRED, expired);
            context.startService(intent);
        }
    }

    public static synchronized <T extends Parcelable> void put(final Context context, final String key, final List<T> objects, final long expired) {
        if (context != null && !StringUtils.isNullOrEmpty(key) && objects != null) {
            final Intent intent = IntentUtils.createActionIntent(context, ParcelableDiskCacheService.class,
                    ACTION_PUT_LIST_EXPIRED);
            intent.putExtra(Intent.EXTRA_TEXT, key);
            final ArrayList<T> list = new ArrayList<>();
            list.addAll(objects);
            intent.putParcelableArrayListExtra(EXTRA_OBJECT, list);
            intent.putExtra(EXTRA_EXPIRED, expired);
            context.startService(intent);
        }
    }

    public static synchronized void clear(final Context context, final String key) {
        if (context != null && !StringUtils.isNullOrEmpty(key)) {
            final Intent intent = IntentUtils.createActionIntent(context, ParcelableDiskCacheService.class,
                    ACTION_CLEAR);
            intent.putExtra(Intent.EXTRA_TEXT, key);
            context.startService(intent);
        }
    }

    public static synchronized void clear(final Context context) {
        if (context != null) {
            final Intent intent = IntentUtils.createActionIntent(context, ParcelableDiskCacheService.class,
                    ACTION_CLEAR_ALL);
            context.startService(intent);
        }
    }

    @Override
    @WorkerThread
    protected <T extends Parcelable> void onHandleIntent(final Intent intent) {
        if (intent == null) {
            return;
        }

        final String action = intent.getAction();

        if (ACTION_PUT.equals(action)) {
            final String key = intent.getStringExtra(Intent.EXTRA_TEXT);
            final T object = intent.getParcelableExtra(EXTRA_OBJECT);
            onHandlePutAction(key, object);
        } else if (ACTION_PUT_LIST.equals(action)) {
            final String key = intent.getStringExtra(Intent.EXTRA_TEXT);
            final List<T> objects = intent.getParcelableArrayListExtra(EXTRA_OBJECT);
            onHandlePutListAction(key, objects);
        } else if (ACTION_PUT_EXPIRED.equals(action)) {
            final String key = intent.getStringExtra(Intent.EXTRA_TEXT);
            final T object = intent.getParcelableExtra(EXTRA_OBJECT);
            final long expired = intent.getLongExtra(EXTRA_EXPIRED, 0);
            onHandlePutAction(key, object, expired);
        } else if (ACTION_PUT_LIST_EXPIRED.equals(action)) {
            final String key = intent.getStringExtra(Intent.EXTRA_TEXT);
            final List<T> objects = intent.getParcelableArrayListExtra(EXTRA_OBJECT);
            final long expired = intent.getLongExtra(EXTRA_EXPIRED, 0);
            onHandlePutListAction(key, objects, expired);
        } else if (ACTION_CLEAR.equals(action)) {
            final String key = intent.getStringExtra(Intent.EXTRA_TEXT);
            onHandleClearAction(key);
        } else if (ACTION_CLEAR_ALL.equals(action)) {
            onHandleClearAction();
        }
    }

    @WorkerThread
    private <T extends Parcelable> void onHandlePutAction(final String key, final T object) {
        final IExpiredParcelableStorage diskCache = Admin.getInstance().get(ParcelableDiskCache.NAME);
        if (diskCache != null) {
            diskCache.put(key, object);
        }
    }

    @WorkerThread
    private <T extends Parcelable> void onHandlePutAction(final String key, final T object, final long expired) {
        final IExpiredParcelableStorage diskCache = Admin.getInstance().get(ParcelableDiskCache.NAME);
        if (diskCache != null) {
            diskCache.put(key, object, expired);
        }
    }

    @WorkerThread
    private <T extends Parcelable> void onHandlePutListAction(final String key, final List<T> object) {
        final IExpiredParcelableStorage diskCache = Admin.getInstance().get(ParcelableDiskCache.NAME);
        if (diskCache != null) {
            diskCache.put(key, object);
        }
    }

    @WorkerThread
    private <T extends Parcelable> void onHandlePutListAction(final String key, final List<T> object, final long expired) {
        final IExpiredParcelableStorage diskCache = Admin.getInstance().get(ParcelableDiskCache.NAME);
        if (diskCache != null) {
            diskCache.put(key, object, expired);
        }
    }

    @WorkerThread
    private void onHandleClearAction(final String key) {
        final IExpiredParcelableStorage diskCache = Admin.getInstance().get(ParcelableDiskCache.NAME);
        if (diskCache != null) {
            diskCache.clear(key);
        }
    }

    @WorkerThread
    private void onHandleClearAction() {
        final IExpiredParcelableStorage diskCache = Admin.getInstance().get(ParcelableDiskCache.NAME);
        if (diskCache != null) {
            diskCache.clear();
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}

