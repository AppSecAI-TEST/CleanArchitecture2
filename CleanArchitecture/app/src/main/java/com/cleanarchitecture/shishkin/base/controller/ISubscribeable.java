package com.cleanarchitecture.shishkin.base.controller;

import android.content.Context;

/**
 * Интерфейс объекта - подписчика, использующего Context
 */
@SuppressWarnings("unused")
public interface ISubscribeable {

    /**
     * Подписать объект
     */
    void subscribe(final Context context);

    /**
     * Отключить подписку объекта
     */
    void unsubscribe(final Context context);

}
