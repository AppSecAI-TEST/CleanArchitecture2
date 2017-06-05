package com.cleanarchitecture.shishkin.base.controller;

/**
 * Интерфейс Lifecycle подписчика.
 */
public interface ILifecycleSubscriber extends ISubscriber {

    /**
     * закрыть activity
     */
    void finish();

}
