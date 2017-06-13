package com.cleanarchitecture.shishkin.base.repository;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.ui.activity.MainActivity;
import com.cleanarchitecture.shishkin.base.controller.Admin;
import com.cleanarchitecture.shishkin.base.controller.AppPreferences;
import com.cleanarchitecture.shishkin.base.controller.EventBusController;
import com.cleanarchitecture.shishkin.base.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.base.event.ClearDiskCacheEvent;
import com.cleanarchitecture.shishkin.base.event.database.DbCreatedEvent;
import com.cleanarchitecture.shishkin.base.event.database.DbUpdatedEvent;
import com.cleanarchitecture.shishkin.base.mail.ShowToastMail;
import com.cleanarchitecture.shishkin.base.storage.DiskCache;
import com.cleanarchitecture.shishkin.base.storage.DiskCacheService;
import com.cleanarchitecture.shishkin.base.storage.IStorage;
import com.cleanarchitecture.shishkin.base.storage.MemoryCache;
import com.cleanarchitecture.shishkin.base.storage.MemoryCacheService;
import com.cleanarchitecture.shishkin.base.utils.AdminUtils;
import com.cleanarchitecture.shishkin.base.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Repository implements IRepository, IModuleSubscriber {
    public static final String NAME = "Repository";

    // информация об источниках данных
    public static final int FROM_CONTENT_PROVIDER = 0; // данные получены из content provider
    public static final int FROM_CACHE = 1; // данные получены из кеша
    public static final int FROM_NETWORK = 2; // данные получены из сети

    // типы кеширования
    public static final int USE_NO_CACHE = 0; // не использовать кеш ни при чтении ни при сохранении данных
    public static final int USE_MEMORY_CACHE = 1; // использовать кеш в памяти при чтении и при сохранении данных - данные будут прочитаны позднее
    public static final int USE_DISK_CACHE = 2; // использовать кеш на диске при чтении и при сохранении данных - данные будут прочитаны позднее
    public static final int USE_MEMORY_AND_DISK_CACHE = 3; // использовать кеш в памяти и на диске при чтении и при сохранении данных - данные будут прочитаны позднее
    public static final int USE_ONLY_MEMORY_CACHE = 4; // использовать только кеш в памяти для получения данных
    public static final int USE_ONLY_DISK_CACHE = 5; // использовать только кеш на диске для получения данных
    public static final int USE_ONLY_CACHE = 6; // использовать только кеш в памяти и на диске для получения данных
    public static final int USE_SAVE_MEMORY_CACHE = 7; // сохранять только в кеше памяти после получения данных. Не использовать кеш для чтения
    public static final int USE_SAVE_DISK_CACHE = 8; // сохранять только в кеше на диске после получения данных. Не использовать кеш для чтения
    public static final int USE_SAVE_CACHE = 9; // сохранять в кеш в памяти и на диске после получения данных. Не использовать кеш для чтения

    private INetProvider mNetProvider;
    private IContentProvider mContentProvider;
    private IDbProvider mDbProvider;

    public Repository() {
        mNetProvider = new NetProvider();
        mContentProvider = new ContentProvider();
        mDbProvider = new DbProvider();
    }

    @Override
    public synchronized Serializable getFromCache(final String key, final int cacheType) {
        final IStorage diskCache = Admin.getInstance().get(DiskCache.NAME);
        final IStorage memoryCache = Admin.getInstance().get(MemoryCache.NAME);

        switch (cacheType) {
            case USE_NO_CACHE:
                break;

            case USE_ONLY_MEMORY_CACHE:
            case USE_MEMORY_CACHE:
                if (memoryCache != null) {
                    return memoryCache.get(key);
                }
                break;

            case USE_ONLY_DISK_CACHE:
            case USE_DISK_CACHE:
                if (diskCache != null) {
                    return diskCache.get(key);
                }
                break;

            case USE_ONLY_CACHE:
            case USE_MEMORY_AND_DISK_CACHE:
                Serializable ser = null;
                if (memoryCache != null) {
                    ser = memoryCache.get(key);
                }
                if (ser == null) {
                    if (diskCache != null) {
                        ser = diskCache.get(key);
                    }
                }
                return ser;
        }
        return null;
    }

    @Override
    public synchronized void putToCache(final String key, final int cacheType, Serializable value) {
        final Context context = AdminUtils.getContext();
        if (context == null) {
            return;
        }

        switch (cacheType) {
            case USE_NO_CACHE:
                break;

            case USE_ONLY_MEMORY_CACHE:
            case USE_SAVE_MEMORY_CACHE:
            case USE_MEMORY_CACHE:
                MemoryCacheService.put(context, key, value);
                break;

            case USE_ONLY_DISK_CACHE:
            case USE_SAVE_DISK_CACHE:
            case USE_DISK_CACHE:
                DiskCacheService.put(context, key, value);
                break;

            case USE_ONLY_CACHE:
            case USE_SAVE_CACHE:
            case USE_MEMORY_AND_DISK_CACHE:
                MemoryCacheService.put(context, key, value);
                DiskCacheService.put(context, key, value);
                break;
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
    public List<String> hasSubscriberType() {
        final ArrayList<String> list = new ArrayList<>();
        list.add(EventBusController.SUBSCRIBER_TYPE);
        return list;
    }

    @Override
    public INetProvider getNetProvider() {
        return mNetProvider;
    }

    @Override
    public IContentProvider getContentProvider() {
        return mContentProvider;
    }

    @Override
    public IDbProvider getDbProvider() {
        return mDbProvider;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onClearDiskCacheEvent(final ClearDiskCacheEvent event) {
        final Context context = AdminUtils.getContext();
        if (context == null) {
            return;
        }

        // раз в сутки очищаем дисковый кэш
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        final int currentDay = StringUtils.toInt(formatter.format(new Date()));
        final int day = StringUtils.toInt(AppPreferences.getLastDayStart(context));
        if (currentDay > day) {
            AppPreferences.setLastDayStart(context, String.valueOf(currentDay));
            final IStorage diskCache = Admin.getInstance().get(DiskCache.NAME);
            if (diskCache != null) {
                diskCache.clearAll();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDbCreatedEvent(final DbCreatedEvent event) {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            AdminUtils.addMail(new ShowToastMail(MainActivity.NAME, context.getString(R.string.db_created, event.getName())));
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDbUpdatedEvent(final DbUpdatedEvent event) {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            AdminUtils.addMail(new ShowToastMail(MainActivity.NAME, context.getString(R.string.db_updated, event.getName())));
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRepositoryRequestGetContactsEvent(final RepositoryRequestGetContactsEvent event) {
        RepositoryContentProvider.requestContacts(event);
    }

}
