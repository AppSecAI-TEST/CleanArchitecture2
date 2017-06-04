package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.ui.activity.IActivity;

/**
 * Интерфейс IActivityController
 */
public interface IActivityController extends IController<IActivity> {

    /**
     * Контроллировать права приложения
     *
     * @param permission право приложения
     * @return the boolean флаг - право приложению предоставлено
     */
    boolean checkPermission(String permission);

    /**
     * Запросить предоставление права приложению
     *
     * @param permission  право приложения
     * @param helpMessage сообщение, выводимое в диалоге предоставления права
     */
    void grantPermission(String permission, String helpMessage);

    /**
     * Контролировать наличие и текущую версию Google Play Services
     */
    boolean checkGooglePlayServices();


}
