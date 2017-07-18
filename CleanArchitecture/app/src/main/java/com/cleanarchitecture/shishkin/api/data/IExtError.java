package com.cleanarchitecture.shishkin.api.data;

@SuppressWarnings("unused")
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
    ExtError setError(String sender, String error);

    /**
     * Установить текст ошибки
     *
     * @param sender источник ошибки
     * @param e      Exception
     * @param error  текст ошибки
     * @return ошибка
     */
    ExtError setError(String sender, Exception e, String error);

    /**
     * Установить текст ошибки
     *
     * @param sender источник ошибки
     * @param e      Exception
     * @param code   код ошибки
     * @return ошибка
     */
    ExtError setError(final String sender, final Exception e, final int code);

    /**
     * Установить код ошибки
     *
     * @param sender источник ошибки
     * @param code   код ошибки
     * @return ошибка
     */
    ExtError setError(String sender, int code);

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
