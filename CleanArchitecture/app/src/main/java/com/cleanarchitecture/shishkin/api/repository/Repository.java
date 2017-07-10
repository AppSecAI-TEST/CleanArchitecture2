package com.cleanarchitecture.shishkin.api.repository;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AbstractModule;
import com.cleanarchitecture.shishkin.api.controller.Admin;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.AppPreferencesUtils;
import com.cleanarchitecture.shishkin.api.controller.EventBusController;
import com.cleanarchitecture.shishkin.api.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.api.event.CheckDiskCacheEvent;
import com.cleanarchitecture.shishkin.api.event.database.DbCreatedEvent;
import com.cleanarchitecture.shishkin.api.event.database.DbUpdatedEvent;
import com.cleanarchitecture.shishkin.api.event.repository.RepositoryRequestGetApplicationSettingsEvent;
import com.cleanarchitecture.shishkin.api.event.repository.RepositoryRequestSetApplicationSettingEvent;
import com.cleanarchitecture.shishkin.api.event.repository.RepositoryResponseGetApplicationSettingsEvent;
import com.cleanarchitecture.shishkin.api.mail.ShowToastMail;
import com.cleanarchitecture.shishkin.api.repository.data.ApplicationSetting;
import com.cleanarchitecture.shishkin.api.storage.IExpiredSerializableStorage;
import com.cleanarchitecture.shishkin.api.storage.SerializableDiskCache;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.ui.activity.MainActivity;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
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

    private synchronized void getApplicationSettings() {
        final Context context = AdminUtils.getContext();
        if (context == null) {
            return;
        }

        ApplicationSetting setting;

        List<ApplicationSetting> list = new LinkedList<>();
        boolean currentValueBoolean = true;

        setting = new ApplicationSetting(ApplicationSetting.TYPE_TEXT)
                .setTitleId(R.string.display);
        list.add(setting);

        currentValueBoolean = AppPreferencesUtils.getSettingShowTooltip(context);
        setting = new ApplicationSetting(ApplicationSetting.TYPE_SWITCH)
                .setTitleId(R.string.settings_show_tooltip)
                .setCurrentValue(String.valueOf(currentValueBoolean))
                .setId(R.id.application_setting_show_tooltip);
        list.add(setting);

        AdminUtils.postEvent(new RepositoryResponseGetApplicationSettingsEvent().setResponse(list));
    }

    private synchronized void setApplicationSetting(RepositoryRequestSetApplicationSettingEvent event) {
        if (event == null) {
            return;
        }

        if (event.getApplicationSetting() == null) {
            return;
        }

        final Context context = AdminUtils.getContext();
        if (context == null) {
            return;
        }

        switch (event.getApplicationSetting().getId()) {
            case R.id.application_setting_show_tooltip:
                final boolean currentValue = Boolean.valueOf(event.getApplicationSetting().getCurrentValue());
                AppPreferencesUtils.setSettingShowTooltip(context, currentValue);
                break;
        }
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
        final int day = StringUtils.toInt(AppPreferencesUtils.getLastDayStart(context));
        if (currentDay > day) {
            final GregorianCalendar calsendar = new GregorianCalendar();
            calsendar.add(GregorianCalendar.MONTH, 1);
            currentDay = StringUtils.toInt(formatter.format(calsendar.getTime()));
            AppPreferencesUtils.setLastDayStart(context, String.valueOf(currentDay));
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
        ContentProviderUtils.requestContacts(event);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRepositoryRequestGetApplicationSettingsEvent(final RepositoryRequestGetApplicationSettingsEvent event) {
        getApplicationSettings();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRepositoryRequestSetApplicationSettingEvent(final RepositoryRequestSetApplicationSettingEvent event) {
        setApplicationSetting(event);
    }
}
