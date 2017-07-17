package com.cleanarchitecture.shishkin.api.event;

import com.cleanarchitecture.shishkin.api.repository.data.ExtError;

@SuppressWarnings("unused")
public interface IEvent {

    /**
     * Получить Id события
     *
     * @return id события
     */
    int getId();

    /**
     * Установить Id события
     *
     * @param id id события
     * @return событие
     */
    IEvent setId(int id);

    /**
     * Получить имя отправителя события
     *
     * @return отправитель события
     */
    String getSender();

    /**
     * Установить имя отправителя события
     *
     * @param sender отправитель события
     * @return событие
     */
    IEvent setSender(final String sender);

    /**
     * Флаг - имеется ли ошибка
     *
     * @return true - устанговлен флаг наличия ошибки
     */
    boolean hasError();

    /**
     * Получить ошибку
     *
     * @return ошибка
     */
    ExtError getError();

    /**
     * Установить ошибку события
     *
     * @param error ошибка
     * @return событие
     */
    IEvent setError(final ExtError error);

}
