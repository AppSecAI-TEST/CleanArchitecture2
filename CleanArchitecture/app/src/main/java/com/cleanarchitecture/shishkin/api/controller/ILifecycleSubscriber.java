package com.cleanarchitecture.shishkin.api.controller;

import com.cleanarchitecture.shishkin.common.lifecycle.IStateable;

/**
 * Интерфейс Lifecycle подписчика.
 */
public interface ILifecycleSubscriber extends ISubscriber, IStateable {

    /**
     * закрыть activity
     */
    void finish();

}
