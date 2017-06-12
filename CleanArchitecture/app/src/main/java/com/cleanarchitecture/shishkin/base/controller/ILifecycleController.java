package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.event.StartActivityEvent;
import com.cleanarchitecture.shishkin.base.ui.activity.AbstractActivity;
import com.cleanarchitecture.shishkin.base.ui.activity.AbstractContentActivity;

/**
 * Интерфейс контроллера Lifecycle приложения.
 */
@SuppressWarnings("unused")
public interface ILifecycleController extends IController<ILifecycleSubscriber> {

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

    /**
     * Обрабатывает событие - Start Activity
     *
     * @param event событие
     */
    void onStartActivityEvent(final StartActivityEvent event);

}
