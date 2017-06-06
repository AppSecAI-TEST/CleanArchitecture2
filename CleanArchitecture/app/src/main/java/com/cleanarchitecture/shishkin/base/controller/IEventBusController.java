package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.event.IEvent;

/**
 * Интерфейс контроллера шины событий приложения
 */
public interface IEventBusController extends ISmallController<Object> {

    /**
     * добавить событие
     *
     * @param event событие
     */
    void post(IEvent event);

    /**
     * добавить постоянное событие
     *
     * @param event событие
     */
    void postSticky(IEvent event);

    /**
     * удалить постоянное событие
     *
     * @param event the event
     */
    void removeSticky(IEvent event);
}
