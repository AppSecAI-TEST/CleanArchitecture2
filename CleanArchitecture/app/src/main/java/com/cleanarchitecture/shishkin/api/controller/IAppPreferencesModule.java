package com.cleanarchitecture.shishkin.api.controller;

public interface IAppPreferencesModule extends IModule {

    int getImageCacheVersion(int defaultValue);

    void setImageCacheVersion(int version);

    boolean getSettingShowTooltip();

    void setSettingShowTooltip(boolean value);

    int getParcelableDiskCacheVersion(int defaultValue);

    void setParcelableDiskCacheVersion(int version);

    String getDesktop();

    void setDesktop(String desktop);

    String getDesktopOrder(String name, String desktopOrder);

    void setDesktopOrder(String name, String desktopOrder);

    String getApplicationVersion();

    void setApplicationVersion(String version);

    String getLastDayStart();

    void setLastDayStart(String day);
}
