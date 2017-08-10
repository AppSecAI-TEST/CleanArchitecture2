package com.cleanarchitecture.shishkin.api.controller;

/**
 * Интерфейс малого контроллера
 *
 * @param <T> the type parameter
 */
public interface ISmallController<T> extends IModule {

    /**
     * Зарегестрировать подписчика
     *
     * @param subscriber подписчик
     */
    void register(T subscriber);

    /**
     * Отключить подписчика
     *
     * @param subscriber подписчик
     */
    void unregister(T subscriber);
}
