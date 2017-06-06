package com.cleanarchitecture.shishkin.base.controller;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Интерфейс абстрактного контроллера
 *
 * @param <T> the type parameter
 */
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

    /**
     * Получить список подписчиков
     *
     * @return список подписчиков
     */
    Map<String, WeakReference<T>> getSubscribers();

    /**
     * Получить текущего подписчика, а если не указан текущий - первого по списку
     *
     * @return подписчик
     */
    T getSubscriber();
}
