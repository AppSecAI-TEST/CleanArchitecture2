package com.cleanarchitecture.shishkin.base.controller;

public interface IModule extends ISubscriber {

    /**
     * Получить имя типа слушателей, которые обрабатывает модуль
     *
     * @return подписчик
     */
    String getSubscriberType();

}
