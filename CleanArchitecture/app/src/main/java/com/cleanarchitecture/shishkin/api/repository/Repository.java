package com.cleanarchitecture.shishkin.api.repository;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AbstractModule;
import com.cleanarchitecture.shishkin.api.controller.Admin;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.ApplicationController;
import com.cleanarchitecture.shishkin.api.controller.EventBusController;
import com.cleanarchitecture.shishkin.api.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.api.event.CheckDiskCacheEvent;
import com.cleanarchitecture.shishkin.api.event.database.DbCreatedEvent;
import com.cleanarchitecture.shishkin.api.event.database.DbUpdatedEvent;
import com.cleanarchitecture.shishkin.api.mail.ShowToastMail;
import com.cleanarchitecture.shishkin.api.storage.IExpiredSerializableStorage;
import com.cleanarchitecture.shishkin.api.storage.SerializableDiskCache;
import com.cleanarchitecture.shishkin.application.ui.activity.MainActivity;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

@SuppressWarnings("unused")
public class Repository extends AbstractModule implements IRepository, IModuleSubscriber {
    public static final String NAME = Repository.class.getName();

    // информация об источниках данных
    public static final int FROM_CONTENT_PROVIDER = 0; // данные получены из content provider
    public static final int FROM_CACHE = 1; // данные получены из кеша
    public static final int FROM_NETWORK = 2; // данные получены из сети

    public Repository() {
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
    public String getDescription() {
        final Context context = ApplicationController.getInstance();
        if (context != null) {
            return context.getString(R.string.module_repository);
        }
        return "Repository";
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onCheckDiskCacheEvent(final CheckDiskCacheEvent event) {
        // раз в месяц чистим дисковый кэш
        final GregorianCalendar calsendar = new GregorianCalendar();
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        int currentDay = StringUtils.toInt(formatter.format(calsendar.getTime()));
        final int day = StringUtils.toInt(AdminUtils.getPreferences().getCleanCacheDay());
        if (currentDay > day) {
            calsendar.add(GregorianCalendar.MONTH, 1);
            currentDay = StringUtils.toInt(formatter.format(calsendar.getTime()));
            AdminUtils.getPreferences().setCleanCacheDay(String.valueOf(currentDay));
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


}
