package com.cleanarchitecture.shishkin.api.controller;

public interface IPreferencesModule extends IModule {

    /**
     * Получить версию кэша картинок
     *
     * @return версия кэша картинок
     */
    int getImageCacheVersion();

    /**
     * Установить версию кэша картинок
     *
     * @param version версия кэша картинок
     */
    void setImageCacheVersion(int version);

    /**
     * Получить флаг - показывать подсказки
     */
    boolean getSettingShowTooltip();

    /**
     * Установить флаг - показывать подсказки
     *
     * @param value true - показывать подсказки
     */
    void setSettingShowTooltip(boolean value);

    /**
     * Получить версию кэша Parcelable
     *
     * @return версия кэша картинок
     */
    int getParcelableDiskCacheVersion();

    /**
     * Установить версию кэша Parcelable
     *
     * @param version версия кэша Parcelable
     */
    void setParcelableDiskCacheVersion(int version);

    /**
     * Получить наименование текущего рабочего стола
     *
     * @return наименование рабочего стола
     */
    String getDesktop();

    /**
     * Установить текущий рабочий стол
     *
     * @param desktop наименование рабочего стола
     */
    void setDesktop(String desktop);

    /**
     * Получить порядок рабочего стола объекта
     *
     * @param name наименование объекта
     * @return порядок рабочего стола объекта по умолчанию
     */
    String getDesktopOrder(String name, String desktopOrder);

    /**
     * Сохранить порядок рабочего стола объекта
     *
     * @param name         наименование объекта
     * @param desktopOrder порядок рабочего стола объекта
     */
    void setDesktopOrder(String name, String desktopOrder);

    /**
     * Получить версию приложения.
     *
     * @return версия приложения
     */
    int getApplicationVersion();

    /**
     * Установить версию приложения.
     *
     * @param version версия приложения
     */
    void setApplicationVersion(int version);

    /**
     * Получить день очистки кэше
     *
     * @return день очистки кэше
     */
    String getCleanCacheDay();

    /**
     * Установить день очистки кэше
     *
     * @param day день очистки кэше
     */
    void setCleanCacheDay(String day);

    String getSettingColor(String key, String defaultColor);

    /**
     * Получить флаг - скриншоты разрешены
     *
     * @return true - скриншоты разрешены
     */
    boolean getScreenshotEnabled();

    /**
     * Установить флаг - скриншоты разрешены
     *
     * @param enabled - true - скриншоты разрешены
     */
    void setScreenshotEnabled(final boolean enabled);

    /**
     * Получить флаг - загрузка модуля разрешена/запрещена
     *
     * @param name имя модуля
     * @return true - загрузка модуля разрешена
     */
    boolean getModule(final String name);

    /**
     * Установить флаг - загрузка модуля разрешена/запрещена
     *
     * @param name      имя модуля
     * @param isEnabled true - загрузка модуля разрешена
     */
    void seModule(final String name, final boolean isEnabled);

    /**
     * Получить ориентацию приложения
     *
     * @return ориентация приложения
     */
    String getOrientation();

    /**
     * Установить ориентацию приложения
     *
     * @param orientation ориентация приложения
     */
    void setOrientation(final String orientation);
}
