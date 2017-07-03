package com.cleanarchitecture.shishkin.api.controller;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Интерфейс абстрактного контроллера
 *
 * @param <T> the type parameter
 */
@SuppressWarnings("unused")
public interface IController<T> extends ISmallController<T> {

    /**
     * Получить подписчика
     *
     * @return подписчик
     */
    T getSubscriber();

    /**
     * Получить подписчика по его имени
     *
     * @param name имя подписчика
     * @return подписчик
     */
    T getSubscriber(final String name);

    /**
     * Получить текущего подписчика (подписчик, у которого состояние = RESUME)
     *
     * @return текущий подписчик
     */
    T getCurrentSubscriber();

    /**
     * Установить текущего подписчика
     *
     * @param subscriber подписчик
     */
    void setCurrentSubscriber(T subscriber);

    /**
     * Получить список подписчиков
     *
     * @return список подписчиков
     */
    Map<String, WeakReference<T>> getSubscribers();

    /**
     * Проверить наличие подписчиков
     *
     * @return true - подписчики есть
     */
    boolean hasSubscribers();


}
