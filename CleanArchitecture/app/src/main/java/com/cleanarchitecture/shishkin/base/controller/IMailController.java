package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.mail.IMail;

import java.util.List;

public interface IMailController extends IController<IMailSubscriber> {

    /**
     * Get mails list for subscriber.
     *
     * @param subscriber the subscriber
     * @return the list
     */
    List<IMail> getMail(IMailSubscriber subscriber);

    /**
     * Add mail.
     *
     * @param mail the mail
     */
    void addMail(IMail mail);

    /**
     * Remove mail.
     *
     * @param mail the mail
     */
    void removeMail(IMail mail);

    /**
     * Clear all mails.
     */
    void clearMail();

}
