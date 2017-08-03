package com.cleanarchitecture.shishkin.api.controller;

public interface INotificationModule {

    /**
     * Добавить сообщщение
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
     * Очистить зону уведомлений
     */
    void clear();

    /**
     * Удалить все сообщения
     */
    void deleteMessages();

    /**
     * установить максимальное кол-во сообщений
     *
     * @param count максимальное количество сообщений
     */
    void setMessagesCount(int count);
}
