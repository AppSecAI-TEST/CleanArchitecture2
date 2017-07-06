package com.cleanarchitecture.shishkin.api.repository;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AbstractModule;
import com.cleanarchitecture.shishkin.api.controller.Admin;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.AppPreferences;
import com.cleanarchitecture.shishkin.api.controller.EventBusController;
import com.cleanarchitecture.shishkin.api.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.api.event.CheckDiskCacheEvent;
import com.cleanarchitecture.shishkin.api.event.database.DbCreatedEvent;
import com.cleanarchitecture.shishkin.api.event.database.DbUpdatedEvent;
import com.cleanarchitecture.shishkin.api.mail.ShowToastMail;
import com.cleanarchitecture.shishkin.api.storage.IExpiredSerializableStorage;
import com.cleanarchitecture.shishkin.api.storage.SerializableDiskCache;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.ui.activity.MainActivity;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@SuppressWarnings("unused")
public class Repository extends AbstractModule implements IRepository, IModuleSubscriber {
    public static final String NAME = Repository.class.getName();

    // информация об источниках данных
    public static final int FROM_CONTENT_PROVIDER = 0; // данные получены из content provider
    public static final int FROM_CACHE = 1; // данные получены из кеша
    public static final int FROM_NETWORK = 2; // данные получены из сети

    // типы кеширования
    public static final int USE_NO_CACHE = 0; // не использовать кеш ни при чтении ни при сохранении данных
    public static final int USE_MEMORY_CACHE = 1; // использовать кеш в памяти при чтении и при сохранении данных - данные будут прочитаны позднее
    public static final int USE_DISK_CACHE = 2; // использовать кеш на диске при чтении и при сохранении данных - данные будут прочитаны позднее
    public static final int USE_CACHE = 3; // использовать кеш в памяти и на диске при чтении и при сохранении данных - данные будут прочитаны позднее
    public static final int USE_ONLY_MEMORY_CACHE = 4; // использовать только кеш в памяти для получения данных
    public static final int USE_ONLY_DISK_CACHE = 5; // использовать только кеш на диске для получения данных
    public static final int USE_ONLY_CACHE = 6; // использовать только кеш в памяти и на диске для получения данных
    public static final int USE_SAVE_MEMORY_CACHE = 7; // сохранять только в кеше памяти после получения данных. Не использовать кеш для чтения
    public static final int USE_SAVE_DISK_CACHE = 8; // сохранять только в кеше на диске после получения данных. Не использовать кеш для чтения
    public static final int USE_SAVE_CACHE = 9; // сохранять в кеш в памяти и на диске после получения данных. Не использовать кеш для чтения

    public Repository() {
    }

    @Override
    public synchronized Serializable getFromCache(final String key, final int cacheType) {
        return Cache.getFromCache(key, cacheType);
    }

    @Override
    public synchronized void putToCache(final String key, final int cacheType, Serializable value, long expired) {
        Cache.putToCache(key, cacheType, value, expired);
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

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onCheckDiskCacheEvent(final CheckDiskCacheEvent event) {
        final Context context = AdminUtils.getContext();
        if (context == null) {
            return;
        }

        // раз в месяц чистим дисковый кэш
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        int currentDay = StringUtils.toInt(formatter.format(new Date()));
        final int day = StringUtils.toInt(AppPreferences.getLastDayStart(context));
        if (currentDay > day) {
            final GregorianCalendar calsendar = new GregorianCalendar();
            calsendar.add(GregorianCalendar.MONTH, 1);
            currentDay = StringUtils.toInt(formatter.format(calsendar.getTime()));
            AppPreferences.setLastDayStart(context, String.valueOf(currentDay));
            final IExpiredSerializableStorage diskCache = Admin.getInstance().get(SerializableDiskCache.NAME);
            if (diskCache != null) {
                diskCache.check();
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
