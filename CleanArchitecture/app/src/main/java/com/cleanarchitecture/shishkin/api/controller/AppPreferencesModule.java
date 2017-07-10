package com.cleanarchitecture.shishkin.api.controller;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.event.repository.RepositoryRequestGetApplicationSettingsEvent;
import com.cleanarchitecture.shishkin.api.event.repository.RepositoryRequestSetApplicationSettingEvent;
import com.cleanarchitecture.shishkin.api.event.repository.RepositoryResponseGetApplicationSettingsEvent;
import com.cleanarchitecture.shishkin.api.repository.data.ApplicationSetting;
import com.cleanarchitecture.shishkin.common.utils.AppPreferencesUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class AppPreferencesModule implements IAppPreferencesModule, IModuleSubscriber {

    public static final String NAME = AppPreferencesModule.class.getName();
    private static final String SETTING_SHOW_TOOLTIP = "setting_show_tooltip";
    private static final String DESKTOP = "desktop";
    private static final String IMAGE_CACHE_VERSION = "image_cache_version";
    private static final String PARCELABLE_CACHE_VERSION = "parcelable_cache_version";
    private static final String VERSION_APPLICATION = "version_application";
    private static final String LAST_DAY_START = "last_day_start";

    private static volatile AppPreferencesModule sInstance;

    public static AppPreferencesModule getInstance() {
        if (sInstance == null) {
            synchronized (AppPreferencesModule.class) {
                if (sInstance == null) {
                    sInstance = new AppPreferencesModule();
                }
            }
        }
        return sInstance;
    }

    private AppPreferencesModule() {
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
    public boolean isPersistent() {
        return true;
    }

    @Override
    public void onUnRegister() {
    }

    @Override
    public List<String> hasSubscriberType() {
        final ArrayList<String> list = new ArrayList<>();
        list.add(EventBusController.SUBSCRIBER_TYPE);
        return list;
    }

    /**
     * Получить версию кэша картинок
     *
     * @param defaultValue значение по умолчанию
     * @return версия кэша картинок
     */
    @Override
    public synchronized int getImageCacheVersion(final int defaultValue) {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            return AppPreferencesUtils.getInt(context, IMAGE_CACHE_VERSION, defaultValue);
        }
        return defaultValue;
    }

    /**
     * Установить версию кэша картинок
     *
     * @param version версия кэша картинок
     */
    @Override
    public synchronized void setImageCacheVersion(final int version) {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            AppPreferencesUtils.putInt(context, IMAGE_CACHE_VERSION, version);
        }
    }

    @Override
    public synchronized boolean getSettingShowTooltip() {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            return AppPreferencesUtils.getBoolean(context, SETTING_SHOW_TOOLTIP, true);
        }
        return true;
    }

    @Override
    public synchronized void setSettingShowTooltip(final boolean value) {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            AppPreferencesUtils.putBoolean(context, SETTING_SHOW_TOOLTIP, value);
        }
    }

    /**
     * Получить версию кэша Parcelable
     *
     * @param defaultValue значение по умолчанию
     * @return версия кэша картинок
     */
    @Override
    public synchronized int getParcelableDiskCacheVersion(final int defaultValue) {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            return AppPreferencesUtils.getInt(context, PARCELABLE_CACHE_VERSION, defaultValue);
        }
        return defaultValue;
    }

    /**
     * Установить версию кэша Parcelable
     *
     * @param version версия кэша Parcelable
     */
    @Override
    public synchronized void setParcelableDiskCacheVersion(final int version) {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            AppPreferencesUtils.putInt(context, PARCELABLE_CACHE_VERSION, version);
        }
    }

    /**
     * Получить рабочий стол
     *
     * @return рабочий стол
     */
    @Override
    public synchronized String getDesktop() {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            return AppPreferencesUtils.getString(context, DESKTOP, "");
        }
        return "";
    }

    /**
     * Установить рабочий стол
     *
     * @param desktop рабочий стол
     */
    @Override
    public synchronized void setDesktop(final String desktop) {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            AppPreferencesUtils.putString(context, DESKTOP, desktop);
        }
    }

    /**
     * Получить порядок рабочего стола
     *
     * @param name рабочий стол
     * @return порядок рабочего стола
     */
    @Override
    public synchronized String getDesktopOrder(final String name, final String desktopOrder) {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            return AppPreferencesUtils.getString(context, name, desktopOrder);
        }
        return desktopOrder;
    }

    /**
     * Сохранить порядок рабочего стола
     *
     * @param name         рабочий стол
     * @param desktopOrder порядок рабочего стола
     */
    @Override
    public synchronized void setDesktopOrder(final String name, final String desktopOrder) {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            AppPreferencesUtils.putString(context, name, desktopOrder);
        }
    }

    /**
     * Получить версию приложения.
     *
     * @return версия приложения
     */
    @Override
    public synchronized String getApplicationVersion() {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            return AppPreferencesUtils.getString(context, VERSION_APPLICATION, null);
        }
        return null;
    }

    /**
     * Установить версию приложения.
     *
     * @param version версия приложения
     */
    @Override
    public synchronized void setApplicationVersion(final String version) {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            AppPreferencesUtils.putString(context, VERSION_APPLICATION, version);
        }
    }

    /**
     * Получить последний день старта приложения
     *
     * @return последний день старта приложения
     */
    @Override
    public synchronized String getLastDayStart() {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            return AppPreferencesUtils.getString(context, LAST_DAY_START, null);
        }
        return null;
    }

    /**
     * Установить последний день старта приложения.
     *
     * @param day версия приложения
     */
    @Override
    public synchronized void setLastDayStart(final String day) {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            AppPreferencesUtils.putString(context, LAST_DAY_START, day);
        }
    }

    private synchronized void getApplicationSettings() {
        ApplicationSetting setting;
        List<ApplicationSetting> list = new LinkedList<>();
        boolean currentValueBoolean = true;

        setting = new ApplicationSetting(ApplicationSetting.TYPE_TEXT)
                .setTitleId(R.string.display);
        list.add(setting);

        currentValueBoolean = getSettingShowTooltip();
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

        switch (event.getApplicationSetting().getId()) {
            case R.id.application_setting_show_tooltip:
                final boolean currentValue = Boolean.valueOf(event.getApplicationSetting().getCurrentValue());
                setSettingShowTooltip(currentValue);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRepositoryRequestSetApplicationSettingEvent(final RepositoryRequestSetApplicationSettingEvent event) {
        setApplicationSetting(event);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRepositoryRequestGetApplicationSettingsEvent(final RepositoryRequestGetApplicationSettingsEvent event) {
        getApplicationSettings();
    }
}
