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
    private static final String LOG_TAG = "AppPreferencesModule:";
    private static final String SETTING_SHOW_TOOLTIP = "setting_show_tooltip";
    private static final String DESKTOP = "desktop";
    private static final String IMAGE_CACHE_VERSION = "image_cache_version";
    private static final String PARCELABLE_CACHE_VERSION = "parcelable_cache_version";
    private static final String VERSION_APPLICATION = "version_application";
    private static final String LAST_DAY_START = "last_day_start";
    private static final String COLOR_ON_NETWORK_DISCONNECTED = "color_on_network_disconnected";

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
        final int currentVersion = ApplicationController.getInstance().getVersion();
        final int version = getApplicationVersion();
        if (version == 0) {
            setApplicationVersion(currentVersion);
        } else if (currentVersion > version) {
            setApplicationVersion(currentVersion);
            ApplicationController.getInstance().onApplicationUpdated(currentVersion);
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

    @Override
    public synchronized int getImageCacheVersion() {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            return AppPreferencesUtils.getInt(context, IMAGE_CACHE_VERSION, 0);
        }
        return 0;
    }

    @Override
    public synchronized void setImageCacheVersion(final int version) {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            AppPreferencesUtils.putInt(context, IMAGE_CACHE_VERSION, version);
        }
    }

    @Override
    public synchronized boolean getSettingShowTooltip() {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            return AppPreferencesUtils.getBoolean(context, SETTING_SHOW_TOOLTIP, true);
        }
        return true;
    }

    @Override
    public synchronized void setSettingShowTooltip(final boolean value) {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            AppPreferencesUtils.putBoolean(context, SETTING_SHOW_TOOLTIP, value);
        }
    }

    @Override
    public synchronized int getParcelableDiskCacheVersion() {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            return AppPreferencesUtils.getInt(context, PARCELABLE_CACHE_VERSION, 0);
        }
        return 0;
    }

    @Override
    public synchronized void setParcelableDiskCacheVersion(final int version) {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            AppPreferencesUtils.putInt(context, PARCELABLE_CACHE_VERSION, version);
        }
    }

    @Override
    public synchronized String getDesktop() {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            return AppPreferencesUtils.getString(context, DESKTOP, "");
        }
        return "";
    }

    @Override
    public synchronized void setDesktop(final String desktop) {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            AppPreferencesUtils.putString(context, DESKTOP, desktop);
        }
    }

    @Override
    public synchronized String getDesktopOrder(final String name, final String desktopOrder) {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            return AppPreferencesUtils.getString(context, name, desktopOrder);
        }
        return desktopOrder;
    }

    @Override
    public synchronized void setDesktopOrder(final String name, final String desktopOrder) {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            AppPreferencesUtils.putString(context, name, desktopOrder);
        }
    }

    @Override
    public synchronized int getApplicationVersion() {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            return AppPreferencesUtils.getInt(context, VERSION_APPLICATION, 0);
        }
        return 0;
    }

    @Override
    public synchronized void setApplicationVersion(final int version) {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            AppPreferencesUtils.putInt(context, VERSION_APPLICATION, version);
        }
    }

    @Override
    public synchronized String getCleanCacheDay() {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            return AppPreferencesUtils.getString(context, LAST_DAY_START, null);
        }
        return null;
    }

    @Override
    public synchronized void setCleanCacheDay(final String day) {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            AppPreferencesUtils.putString(context, LAST_DAY_START, day);
        }
    }

    @Override
    public synchronized String getSettingColorOnNetworkDisconnected() {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            return AppPreferencesUtils.getString(context, COLOR_ON_NETWORK_DISCONNECTED, "#ff5514");
        }
        return null;
    }

    private synchronized void getApplicationSettings() {
        ApplicationSetting setting;
        List<ApplicationSetting> list = new LinkedList<>();
        boolean currentValueBoolean = true;
        String currentValueString;

        setting = new ApplicationSetting(ApplicationSetting.TYPE_TEXT)
                .setTitleId(R.string.display);
        list.add(setting);

        currentValueBoolean = getSettingShowTooltip();
        setting = new ApplicationSetting(ApplicationSetting.TYPE_SWITCH)
                .setTitleId(R.string.settings_show_tooltip)
                .setCurrentValue(String.valueOf(currentValueBoolean))
                .setId(R.id.application_setting_show_tooltip);
        list.add(setting);

        setting = new ApplicationSetting(ApplicationSetting.TYPE_TEXT)
                .setTitleId(R.string.settings_color);
        list.add(setting);

        currentValueString = getSettingColorOnNetworkDisconnected();
        setting = new ApplicationSetting(ApplicationSetting.TYPE_COLOR)
                .setTitleId(R.string.settings_color_on_disconnect_network)
                .setCurrentValue(currentValueString)
                .setId(R.id.application_setting_color_on_network_disconnected);
        list.add(setting);


        EventBusController.getInstance().post(new RepositoryResponseGetApplicationSettingsEvent().setResponse(list));
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
