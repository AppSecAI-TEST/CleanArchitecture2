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
     * Установить рабочий стол
     *
     * @param desktop имя рабочего стола
     */
    void setDesktop(String desktop);

    /**
     * Вызвать диалог установки рабочего стола
     */
    void getDesktop();
}
