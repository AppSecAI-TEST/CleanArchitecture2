package com.cleanarchitecture.shishkin.api.controller;

public interface IModule extends ISubscriber {

    /**
     * Получить имя типа слушателей, которые обрабатывает модуль
     *
     * @return имя типа обрабатываемых слушателей
     */
    String getSubscriberType();

    /**
     * Получить тип модуля
     *
     * @return true - не будет удаляться администратором
     */
    boolean isPersistent();

    /**
     * Событие - отключить регистрацию
     */
    void onUnRegister();

    /**
     * Получить описание модуля
     *
     * @return описание
     */
    String getDescription();

}
