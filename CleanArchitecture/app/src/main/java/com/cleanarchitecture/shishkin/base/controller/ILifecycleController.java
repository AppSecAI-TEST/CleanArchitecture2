package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.ui.activity.AbstractActivity;
import com.cleanarchitecture.shishkin.base.ui.activity.AbstractContentActivity;

/**
 * Интерфейс контроллера Lifecycle приложения.
 */
public interface ILifecycleController extends ISubscriber {

    /**
     * Зарегестрировать подписчика
     *
     * @param subscriber подписчик
     */
    void register(ILifecycleSubscriber subscriber);

    /**
     * Отключить подписчика
     *
     * @param subscriber подписчик
     */
    void unregister(ILifecycleSubscriber subscriber);

    /**
     * Получить AbstractActivity
     *
     * @return the AbstractActivity
     */
    AbstractActivity getActivity();

    /**
     * Получить текущую AbstractActivity.
     *
     * @return текущая AbstractActivity
     */
    AbstractActivity getCurrentActivity();

    /**
     * Установить текущего подписчика
     *
     * @param subscriber подписчик
     */
    void setCurrentSubscriber(ILifecycleSubscriber subscriber);


    /**
     * Получить AbstractContentActivity
     *
     * @return AbstractContentActivity
     */
    AbstractContentActivity getContentActivity();

    /**
     * Получить текущую AbstractContentActivity
     *
     * @return текущая AbstractContentActivity
     */
    AbstractContentActivity getCurrentContentActivity();

}
