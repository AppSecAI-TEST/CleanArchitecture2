package com.cleanarchitecture.shishkin.api.controller;

/**
 * Интерфейс Lifecycle подписчика.
 */
public interface ILifecycleSubscriber extends ISubscriber {

    /**
     * закрыть activity
     */
    void finish();

}
