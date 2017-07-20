package com.cleanarchitecture.shishkin.api.controller;

public interface INotificationModule {

    /**
     * Добавить сервис уведомлений.
     *
     * @param name имя сервиса
     * @param clss Сlass сервиса
     */
    void addService(final String name, final Class clss);

    /**
     * Добавить сообщщение.
     *
     * @param message текст сообщения
     */
    void addMessage(final String message);


    /**
     * Добавить сообщение, если его нет в списке сообщений
     *
     * @param message текст сообщения
     */
    void addDistinctMessage(final String message);

    /**
     * Заменить сообщение
     *
     * @param message текст сообщения
     */
    void replaceMessage(final String message);

    /**
     * Обновить
     */
    void refresh();

    /**
     * Очистить
     */
    void clear();

    /**
     * установить максимальное кол-во сообщений
     */
    void setMessagesCount(final int count);
}
