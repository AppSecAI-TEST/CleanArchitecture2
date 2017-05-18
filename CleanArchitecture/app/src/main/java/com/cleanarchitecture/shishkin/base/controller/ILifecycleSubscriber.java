package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.lifecycle.ILifecycle;

/**
 * Интерфейс Lifecycle подписчика.
 */
public interface ILifecycleSubscriber extends ISubscriber{

    /**
     * закрыть activity
     */
    void finish();

    /**
     * Зарегистрировать ILifecycle объект
     *
     * @param object ILifecycle объект
     */
    void registerLifecycleObject(final ILifecycle object);

}
