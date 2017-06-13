package com.cleanarchitecture.shishkin.api.event;

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
     * @param sender отправитель события
     * @param error  текст ошибки
     * @return событие
     */
    IEvent setErrorText(String sender, String error);

    /**
     * Установить текст ошибки
     *
     * @param sender отправитель события
     * @param e      Exception
     * @param error  текст ошибки
     * @return событие
     */
    IEvent setErrorText(String sender, Exception e, String error);

    /**
     * Получить код ошибки
     *
     * @return код ошибки
     */
    int getErrorCode();

    /**
     * Установить код ошибки
     *
     * @param sender отправитель события
     * @param code   код ошибки
     * @return событие
     */
    IEvent setErrorCode(String sender, int code);

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

}
