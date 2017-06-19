package com.cleanarchitecture.shishkin.api.controller;

import java.lang.ref.WeakReference;
import java.util.Map;

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

    /**
     * Получить список подписчиков
     *
     * @return список подписчиков
     */
    Map<String, WeakReference<T>> getSubscribers();

    /**
     * Проверить наличие подписчика
     *
     * @param subscriber подписчик
     * @return true - подписчик найден
     */
    boolean isRegistered(T subscriber);

}
