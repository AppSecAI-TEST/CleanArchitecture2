package com.cleanarchitecture.shishkin.api.controller;

import java.util.List;

/**
 * Интерфейс объекта, который регистрируется в модулях
 */
public interface IModuleSubscriber extends ISubscriber {

    /**
     * Список имен модулей, в которых должен быть зарегистрирован объект
     *
     * @return список имен модулей
     */
    List<String> hasSubscriberType();

}
