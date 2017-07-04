package com.cleanarchitecture.shishkin.api.controller;

public interface IDesktopController extends IModule {

    /**
     * Получить layout id.
     *
     * @param name      имя layout
     * @param defaultId default layout id
     * @return layout id
     */
    int getLayoutId(String name, int defaultId);

    /**
     * Получить style id.
     *
     * @param name      имя style
     * @param defaultId default style id
     * @return style id
     */
    int getStyleId(final String name, final int defaultId);

    /**
     * Получить menu id.
     *
     * @param name      имя menu
     * @param defaultId default menu id
     * @return menu id
     */
    int getMenuId(final String name, final int defaultId);

    /**
     * Получить ресурс id.
     *
     * @param name      имя ресурса
     * @param type      тип ресурса
     * @param defaultId id ресурса по умолчанию
     * @return id ресурса
     */
    int getResourceId(final String name, final String type, final int defaultId);

    /**
     * Установить рабочий стол
     *
     * @param desktop имя рабочего стола
     */
    void setDesktop(String desktop);

    /**
     * Вызвать диалог установки рабочего стола
     */
    void getDesktop();

    /**
     * Получить порядок элементов рабочего стола
     *
     * @param subscriber подписчик
     * @return порядок элементов рабочего стола
     */
    String getDesktopOrder(IDesktopSubscriber subscriber);

    /**
     * Установить порядок элементов рабочего стола
     *
     * @param subscriber подписчик
     */
    void setDesktopOrder(IDesktopSubscriber subscriber);
}
