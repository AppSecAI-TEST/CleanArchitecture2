package com.cleanarchitecture.shishkin.api.controller;

public interface IModule extends ISubscriber {

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
