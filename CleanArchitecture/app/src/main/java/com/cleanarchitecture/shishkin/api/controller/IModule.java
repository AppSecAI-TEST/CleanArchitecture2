package com.cleanarchitecture.shishkin.api.controller;

public interface IModule extends ISubscriber {

    /**
     * Получить имя типа слушателей, которые обрабатывает модуль
     *
     * @return имя типа обрабатываемых слушателей
     */
    String getSubscriberType();

}
