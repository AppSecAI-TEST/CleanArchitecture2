package com.cleanarchitecture.shishkin.base.controller;

/**
 * The interface Error controller.
 */
public interface IErrorController extends ISubscriber {

    /**
     * Ошибка
     *
     * @param source источник ошибки
     * @param e      Exception
     */
    void onError(String source, Exception e);

    /**
     * Ошибка
     *
     * @param source    источник ошибки
     * @param throwable Throwable
     */
    void onError(String source, Throwable throwable);

    /**
     * Ошибка
     *
     * @param source         источник ошибки
     * @param e              Exception
     * @param displayMessage текст ошибки пользователю
     */
    void onError(String source, Exception e, String displayMessage);

    /**
     * Ошибка
     *
     * @param source    источник ошибки
     * @param e         Exception
     * @param errorCode код ошибки
     */
    void onError(String source, Exception e, int errorCode);

    /**
     * Ошибка
     *
     * @param source    источник ошибки
     * @param errorCode код ошибки
     */
    void onError(String source, int errorCode);

    /**
     * Ошибка
     *
     * @param source         источник ошибки
     * @param displayMessage текст ошибки пользователю
     */
    void onError(String source, String displayMessage);
}
