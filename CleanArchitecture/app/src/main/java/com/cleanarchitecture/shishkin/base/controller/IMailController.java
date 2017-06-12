package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.mail.IMail;

import java.util.List;

@SuppressWarnings("unused")
public interface IMailController extends IController<IMailSubscriber> {

    /**
     * Получить почту подписчика
     *
     * @param subscriber the subscriber
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

}
