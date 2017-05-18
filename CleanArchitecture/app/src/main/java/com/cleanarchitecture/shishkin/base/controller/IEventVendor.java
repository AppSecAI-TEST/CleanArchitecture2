package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.event.IEvent;

/**
 * Интерфейс рассыльщика событий
 */
public interface IEventVendor {
    /**
     * послать событие на шину событий
     *
     * @param event событие
     */
    void postEvent(IEvent event);
}
