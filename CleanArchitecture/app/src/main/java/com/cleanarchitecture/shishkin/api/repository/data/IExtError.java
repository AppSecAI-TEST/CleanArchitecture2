package com.cleanarchitecture.shishkin.api.repository.data;

public interface IExtError {
    /**
     * Получить текст ошибки
     *
     * @return текст ошибки
     */
    String getErrorText();

    /**
     * Установить текст ошибки
     *
     * @param sender источник ошибки
     * @param error  текст ошибки
     * @return ошибка
     */
    ExtError setErrorText(String sender, String error);

    /**
     * Установить текст ошибки
     *
     * @param sender источник ошибки
     * @param e      Exception
     * @param error  текст ошибки
     * @return ошибка
     */
    ExtError setErrorText(String sender, Exception e, String error);

    /**
     * Получить код ошибки
     *
     * @return код ошибки
     */
    int getErrorCode();

    /**
     * Установить код ошибки
     *
     * @param sender источник ошибки
     * @param code   код ошибки
     * @return ошибка
     */
    ExtError setErrorCode(String sender, int code);

    /**
     * Флаг - имеется ли ошибка
     *
     * @return true - устанговлен флаг наличия ошибки
     */
    boolean hasError();

    /**
     * Получить имя источника ошибки
     *
     * @return имя источника ошибки
     */
    String getSender();

    /**
     * Установить имя источника ошибки
     *
     * @param sender источник ошибки
     * @return ошибка
     */
    ExtError setSender(final String sender);

}
