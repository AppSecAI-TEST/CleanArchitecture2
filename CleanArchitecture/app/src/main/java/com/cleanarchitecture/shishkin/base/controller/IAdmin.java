package com.cleanarchitecture.shishkin.base.controller;

/**
 * Итерфейс администратора
 */
public interface IAdmin extends ISubscriber {

    /**
     * Получить модуль
     *
     * @param <C>        тип модуля
     * @param nameModule имя модуля
     * @return модуль
     */
    <C> C getModule(String nameModule);

    /**
     * Зарегистрировать модуль
     *
     * @param module модуль
     */
    void registerModule(IModule module);

    /**
     * Отменить регистрацию модуля
     *
     * @param nameModule имя модуля
     */
    void unregisterModule(String nameModule);

    /**
     * Отменить регистрацию модуля
     *
     * @param module имя модуля
     */
    void unregisterModule(IModule module);

    /**
     * Зарегистрировать подписчика модуля
     *
     * @param subscriber подписчик модуля
     */
    void register(IModuleSubscriber subscriber);

    /**
     * Отменить регистрацию подписчика модуля
     *
     * @param subscriber подписчик модуля
     */
    void unregister(IModuleSubscriber subscriber);

    /**
     * Установить подписчика текущим
     *
     * @param subscriber подписчик
     */
    void setCurrentSubscriber(IModuleSubscriber subscriber);
}
