package com.cleanarchitecture.shishkin.base.event;

@SuppressWarnings("unused")
public interface IEvent {

    /**
     * Получить текст ошибки
     *
     * @return текст ошибки
     */
    String getErrorText();

    /**
     * Установить текст ошибки
     *
     * @param error текст ошибки
     * @return событие
     */
    IEvent setErrorText(String error);

    /**
     * Получить код ошибки
     *
     * @return код ошибки
     */
    int getErrorCode();

    /**
     * Установить код ошибки
     *
     * @param code код ошибки
     * @return событие
     */
    IEvent setErrorCode(int code);

    /**
     * Флаг - имеет ли событие ошибку
     *
     * @return true - устанговлен флаг наличия ошибки
     */
    boolean hasError();

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
     * Получить отправителя события
     *
     * @return отправитель события
     */
    Object getSender();

    /**
     * Установить отправителя события
     *
     * @param sender отправитель события
     * @return событие
     */
    IEvent setSender(final Object sender);

}
