package com.cleanarchitecture.shishkin.api.controller;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.data.ApplicationSetting;
import com.cleanarchitecture.shishkin.api.event.repository.RepositoryRequestGetApplicationSettingsEvent;
import com.cleanarchitecture.shishkin.api.event.repository.RepositoryRequestSetApplicationSettingEvent;
import com.cleanarchitecture.shishkin.api.event.repository.RepositoryResponseGetApplicationSettingsEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowDialogEvent;
import com.cleanarchitecture.shishkin.api.service.BadgeService;
import com.cleanarchitecture.shishkin.api.service.BoardService;
import com.cleanarchitecture.shishkin.api.service.NotificationService;
import com.cleanarchitecture.shishkin.common.utils.AppPreferencesUtils;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class PreferencesModule implements IPreferencesModule, IModuleSubscriber {

    public static final String NAME = PreferencesModule.class.getName();
    private static final String LOG_TAG = "PreferencesModule:";
    private static final String SETTING_SHOW_TOOLTIP = "setting_show_tooltip";
    private static final String DESKTOP = "desktop";
    private static final String IMAGE_CACHE_VERSION = "image_cache_version";
    private static final String PARCELABLE_CACHE_VERSION = "parcelable_cache_version";
    private static final String VERSION_APPLICATION = "version_application";
    private static final String LAST_DAY_START = "last_day_start";
    public static final String COLOR_ON_NETWORK_CONNECTED = "color_on_network_connected";
    public static final String COLOR_ON_NETWORK_DISCONNECTED = "color_on_network_disconnected";
    public static final String SCREENSHOT = "screenshot";

    private static volatile PreferencesModule sInstance;

    public static PreferencesModule getInstance() {
        if (sInstance == null) {
            synchronized (PreferencesModule.class) {
                if (sInstance == null) {
                    sInstance = new PreferencesModule();
                }
            }
        }
        return sInstance;
    }

    private PreferencesModule() {
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
    public synchronized boolean getScreenshotEnabled() {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            return AppPreferencesUtils.getBoolean(context, SCREENSHOT, true);
        }
        return true;
    }

    @Override
    public synchronized void setScreenshotEnabled(final boolean enabled) {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            AppPreferencesUtils.putBoolean(context, SCREENSHOT, enabled);
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
    public synchronized boolean getModule(final String name) {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            return AppPreferencesUtils.getBoolean(context, name, true);
        }
        return true;
    }

    @Override
    public synchronized void seModule(final String name, final boolean isEnabled) {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            AppPreferencesUtils.putBoolean(context, name, isEnabled);
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
    public synchronized String getSettingColor(final String key, final String defaultColor) {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            return AppPreferencesUtils.getString(context, key, defaultColor);
        }
        return null;
    }

    public synchronized void setSettingColor(final ApplicationSetting setting) {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            AppPreferencesUtils.putString(context, setting.getPreferenceName(), String.valueOf(setting.getCurrentValue()));
        }
    }

    public synchronized void setSettingModule(final ApplicationSetting setting) {
        Admin.getInstance().unregisterModule(NotificationModule.NAME);
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            AppPreferencesUtils.putBoolean(context, setting.getPreferenceName(), Boolean.valueOf(setting.getCurrentValue()));
        }
        Admin.getInstance().registerModule(NotificationModule.NAME);
    }

    private synchronized void getApplicationSettings() {
        final Context context = AdminUtils.getContext();
        if (context == null) {
            return;
        }

        ApplicationSetting setting;
        List<ApplicationSetting> list = new LinkedList<>();
        boolean currentValueBoolean = true;
        String currentValueString;

        setting = new ApplicationSetting(ApplicationSetting.TYPE_TEXT)
                .setTitle(AdminUtils.getString(R.string.display));
        list.add(setting);

        currentValueBoolean = getSettingShowTooltip();
        setting = new ApplicationSetting(ApplicationSetting.TYPE_SWITCH)
                .setTitle(AdminUtils.getString(R.string.settings_show_tooltip))
                .setCurrentValue(String.valueOf(currentValueBoolean))
                .setId(R.id.application_setting_show_tooltip);
        list.add(setting);

        currentValueBoolean = getScreenshotEnabled();
        setting = new ApplicationSetting(ApplicationSetting.TYPE_SWITCH)
                .setTitle(AdminUtils.getString(R.string.settings_show_screenshot))
                .setCurrentValue(String.valueOf(currentValueBoolean))
                .setId(R.id.application_setting_screenshot_enabled);
        list.add(setting);

        setting = new ApplicationSetting(ApplicationSetting.TYPE_TEXT)
                .setTitle(AdminUtils.getString(R.string.settings_color));
        list.add(setting);

        currentValueString = getSettingColor(COLOR_ON_NETWORK_CONNECTED, String.valueOf(ViewUtils.getColor(context, R.color.blue)));
        setting = new ApplicationSetting(ApplicationSetting.TYPE_COLOR)
                .setTitle(AdminUtils.getString(R.string.settings_color_on_connect_network))
                .setPreferenceName(COLOR_ON_NETWORK_CONNECTED)
                .setCurrentValue(currentValueString)
                .setDefaultValue(String.valueOf(ViewUtils.getColor(context, R.color.blue)))
                .setId(R.id.application_setting_color);
        list.add(setting);

        currentValueString = getSettingColor(COLOR_ON_NETWORK_DISCONNECTED, String.valueOf(ViewUtils.getColor(context, R.color.orange)));
        setting = new ApplicationSetting(ApplicationSetting.TYPE_COLOR)
                .setTitle(AdminUtils.getString(R.string.settings_color_on_disconnect_network))
                .setPreferenceName(COLOR_ON_NETWORK_DISCONNECTED)
                .setCurrentValue(currentValueString)
                .setDefaultValue(String.valueOf(ViewUtils.getColor(context, R.color.orange)))
                .setId(R.id.application_setting_color);
        list.add(setting);

        setting = new ApplicationSetting(ApplicationSetting.TYPE_TEXT)
                .setTitle(AdminUtils.getString(R.string.modules));
        list.add(setting);

        currentValueBoolean = getModule(NotificationService.NAME);
        setting = new ApplicationSetting(ApplicationSetting.TYPE_SWITCH)
                .setPreferenceName(NotificationService.NAME)
                .setTitle(AdminUtils.getString(R.string.service_notification))
                .setCurrentValue(String.valueOf(currentValueBoolean))
                .setId(R.id.application_setting_modules);
        list.add(setting);

        currentValueBoolean = getModule(BoardService.NAME);
        setting = new ApplicationSetting(ApplicationSetting.TYPE_SWITCH)
                .setTitle(AdminUtils.getString(R.string.service_board))
                .setPreferenceName(BoardService.NAME)
                .setCurrentValue(String.valueOf(currentValueBoolean))
                .setId(R.id.application_setting_modules);
        list.add(setting);

        currentValueBoolean = getModule(BadgeService.NAME);
        setting = new ApplicationSetting(ApplicationSetting.TYPE_SWITCH)
                .setTitle(AdminUtils.getString(R.string.service_badger))
                .setPreferenceName(BadgeService.NAME)
                .setCurrentValue(String.valueOf(currentValueBoolean))
                .setId(R.id.application_setting_modules);
        list.add(setting);

        currentValueBoolean = getModule(LocationController.NAME);
        setting = new ApplicationSetting(ApplicationSetting.TYPE_SWITCH)
                .setTitle(AdminUtils.getModuleDescription(LocationController.NAME))
                .setPreferenceName(LocationController.NAME)
                .setCurrentValue(String.valueOf(currentValueBoolean))
                .setId(R.id.application_setting_modules);
        list.add(setting);

        EventBusController.getInstance().post(new RepositoryResponseGetApplicationSettingsEvent().setResponse(list));
    }

    private synchronized void setApplicationSetting(RepositoryRequestSetApplicationSettingEvent event) {
        if (event == null) {
            return;
        }

        final Context context = AdminUtils.getContext();

        if (event.getApplicationSetting() == null) {
            return;
        }

        boolean currentValue = true;
        switch (event.getApplicationSetting().getId()) {
            case R.id.application_setting_show_tooltip:
                currentValue = Boolean.valueOf(event.getApplicationSetting().getCurrentValue());
                setSettingShowTooltip(currentValue);
                break;

            case R.id.application_setting_screenshot_enabled:
                currentValue = Boolean.valueOf(event.getApplicationSetting().getCurrentValue());
                setScreenshotEnabled(currentValue);
                if (context != null) {
                    AdminUtils.postEvent(new ShowDialogEvent(-1, null, context.getString(R.string.screenshot_help)));
                }
                break;

            case R.id.application_setting_color:
                setSettingColor(event.getApplicationSetting());
                break;

            case R.id.application_setting_modules:
                if (context != null) {
                    AdminUtils.postEvent(new ShowDialogEvent(-1, null, context.getString(R.string.screenshot_help)));
                }
                setSettingModule(event.getApplicationSetting());
                break;
        }

        getApplicationSettings();
    }

    @Override
    public String getDescription() {
        final Context context = ApplicationController.getInstance();
        if (context != null) {
            return context.getString(R.string.module_preferences);
        }
        return "Preferences Module";
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
