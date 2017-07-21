package com.cleanarchitecture.shishkin.api.controller;

public interface INotificationModule {

    /**
     * Добавить сервис уведомлений.
     *
     * @param name имя сервиса
     * @param clss Сlass сервиса
     */
    void addService(String name, Class clss);

    /**
     * Добавить сообщщение.
     *
     * @param message текст сообщения
     */
    void addMessage(String message);


    /**
     * Добавить сообщение, если его нет в списке сообщений
     *
     * @param message текст сообщения
     */
    void addDistinctMessage(String message);

    /**
     * Заменить сообщение
     *
     * @param message текст сообщения
     */
    void replaceMessage(String message);

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
     *
     * @param name имя сервиса
     * @param count максимальное количество сообщений
     */
    void setMessagesCount(String name, int count);
}
