package com.cleanarchitecture.shishkin.api.controller;

/**
 * Интерфейс абстрактного контроллера
 *
 * @param <T> the type parameter
 */
@SuppressWarnings("unused")
public interface IController<T> extends ISmallController<T> {

    /**
     * Установить текущего подписчика
     *
     * @param subscriber подписчик
     */
    void setCurrentSubscriber(T subscriber);

    /**
     * Получить текущего подписчика
     *
     * @return текущий подписчик
     */
    T getCurrentSubscriber();
}
