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
     * Добавить сообщщение всем.
     *
     * @param message текст сообщения
     */
    void addMessageAll(String message);

    /**
     * Добавить сообщщение
     *
     * @param name    имя сервиса
     * @param message текст сообщения
     */
    void addMessage(String name, String message);

    /**
     * Добавить сообщение всем, если его нет в списке сообщений
     *
     * @param message текст сообщения
     */
    void addDistinctMessageAll(String message);

    /**
     * Добавить сообщение, если его нет в списке сообщений
     *
     * @param name    имя сервиса
     * @param message текст сообщения
     */
    void addDistinctMessage(String name, String message);

    /**
     * Заменить сообщение всем
     *
     * @param message текст сообщения
     */
    void replaceMessageAll(String message);

    /**
     * Заменить сообщение
     *
     * @param name    имя сервиса
     * @param message текст сообщения
     */
    void replaceMessage(String name, String message);

    /**
     * Обновить все
     */
    void refreshAll();

    /**
     * Обновить
     *
     * @param name имя сервиса
     */
    void refresh(String name);

    /**
     * Очистить все
     */
    void clearAll();

    /**
     * Очистить
     *
     * @param name имя сервиса
     */
    void clear(String name);

    /**
     * установить максимальное кол-во сообщений
     *
     * @param name  имя сервиса
     * @param count максимальное количество сообщений
     */
    void setMessagesCount(String name, int count);
}
