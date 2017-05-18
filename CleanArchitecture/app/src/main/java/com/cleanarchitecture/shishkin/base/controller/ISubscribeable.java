package com.cleanarchitecture.shishkin.base.controller;

import android.content.Context;

/**
 * Interface defines subscribeable behavior of concrete implementation.
 */
public interface ISubscribeable {

    /**
     * Зарегистрировать компонент
     */
    void subscribe(final Context context);

    /**
     * Отключить компонент
     */
    void unsubscribe(final Context context);

}
