package com.cleanarchitecture.shishkin.base.controller;

import android.content.Context;

import com.cleanarchitecture.shishkin.base.ui.activity.IActivity;

/**
 * Интерфейс IActivityController
 */
public interface IActivityController extends ISubscriber {

    /**
     * Зарегестрировать подписчика
     *
     * @param subscriber подписчик
     */
    void register(IActivity subscriber);

    /**
     * Отключить подписчика
     *
     * @param subscriber подписчик
     */
    void unregister(IActivity subscriber);

    /**
     * Получить context.
     *
     * @return the context
     */
    Context getContext();

    /**
     * Получить подписчика
     *
     * @return подписчик
     */
    IActivity getSubscriber();

    /**
     * Получить текущего подписчика
     *
     * @return текущий подписчик
     */
    IActivity getCurrentSubscriber();

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
     * Установить текущего подписчика
     *
     * @param subscriber подписчик
     */
    void setCurrentSubscriber(IActivity subscriber);

}
