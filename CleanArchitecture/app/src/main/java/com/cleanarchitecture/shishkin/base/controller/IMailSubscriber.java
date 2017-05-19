package com.cleanarchitecture.shishkin.base.controller;

public interface IMailSubscriber extends ISubscriber {
    /**
     * Read mail by subscriber.
     */
    void readMail();
}
