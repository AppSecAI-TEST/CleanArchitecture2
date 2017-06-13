package com.cleanarchitecture.shishkin.api.controller;

/**
 * Итерфейс администратора
 */
@SuppressWarnings("unused")
public interface IAdmin extends ISubscriber {

    /**
     * Получить объект
     *
     * @param <C>  тип объекта
     * @param name имя объекта
     * @return модуль
     */
    <C> C get(String name);

    /**
     * Зарегистрировать модуль
     *
     * @param module модуль
     */
    void registerModule(IModule module);

    /**
     * Отменить регистрацию модуля или объекта
     *
     * @param name имя модуля/объекта
     */
    void unregister(String name);

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
