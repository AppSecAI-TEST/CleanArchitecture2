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
     * Добавить сообщщение
     *
     * @param name    имя сервиса
     * @param message текст сообщения
     */
    void addMessage(String name, String message, boolean isNotification);

    /**
     * Добавить сообщение, если его нет в списке сообщений
     *
     * @param name    имя сервиса
     * @param message текст сообщения
     */
    void addDistinctMessage(String name, String message, boolean isNotification);

    /**
     * Заменить сообщение
     *
     * @param name    имя сервиса
     * @param message текст сообщения
     */
    void replaceMessage(String name, String message, boolean isNotification);

    /**
     * Обновить
     *
     * @param name имя сервиса
     */
    void refresh(String name, boolean isNotification);

    /**
     * Очистить все
     */
    void clearAll();

    /**
     * Очистить
     *
     * @param name имя сервиса
     */
    void clear(String name, boolean isNotification);

    /**
     * установить максимальное кол-во выводимых сообщений
     *
     * @param name  имя сервиса
     * @param count максимальное количество сообщений
     */
    void setMessagesCount(String name, int count, boolean isNotification);
}
