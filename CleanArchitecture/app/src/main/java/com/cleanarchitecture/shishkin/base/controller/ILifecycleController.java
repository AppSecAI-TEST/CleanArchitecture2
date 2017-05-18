package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.presenter.ActivityPresenter;
import com.cleanarchitecture.shishkin.base.ui.activity.AbstractActivity;

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
     * Получить текущий ActivityPresenter
     *
     * @return текущий ActivityPresenter
     */
    ActivityPresenter getActivityPresenter();

    /**
     * Установить текущего подписчика
     *
     * @param subscriber подписчик
     */
    void setCurrentSubscriber(ILifecycleSubscriber subscriber);
}
