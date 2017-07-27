package com.cleanarchitecture.shishkin.api.controller;

import com.cleanarchitecture.shishkin.api.mail.IMail;

import java.util.List;

@SuppressWarnings("unused")
public interface IMailController extends IController<IMailSubscriber> {

    /**
     * Получить почту подписчика
     *
     * @param subscriber подписчик
     * @return the list
     */
    List<IMail> getMail(IMailSubscriber subscriber);

    /**
     * Добавить почтовое сообщение
     *
     * @param mail the mail
     */
    void addMail(IMail mail);

    /**
     * Удалить почтовое сообщение
     *
     * @param mail the mail
     */
    void removeMail(IMail mail);

    /**
     * Удалить все сообщения
     */
    void clearMail();

    /**
     * Удалить сообщения подписчика
     *
     * @param subscriber подписчик
     */
    void clearMail(final IMailSubscriber subscriber);

}
