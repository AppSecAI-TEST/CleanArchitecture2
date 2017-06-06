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
    <C> C getModule(final String nameModule);

    /**
     * Зарегистрировать модуль
     *
     * @param module модуль
     */
    void registerModule(final IModule module);

    /**
     * Отменить регистрациюмодуля
     *
     * @param nameModule имя модуля
     */
    void unregisterModule(final String nameModule);

    /**
     * Зарегистрировать подписчика модуля
     *
     * @param subscriber подписчик
     */
    void register(final IModuleSubscriber subscriber);

    /**
     * Отменить регистрацию подписчика
     *
     * @param subscriber подписчик
     */
    void unregister(final IModuleSubscriber subscriber);

    /**
     * Установить подписчика текущим
     *
     * @param subscriber подписчик
     */
    void setCurrentSubscriber(final IModuleSubscriber subscriber);
}
