package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.lifecycle.IStateable;

public interface IMailSubscriber extends ISubscriber, IStateable {
    /**
     * Read mail by subscriber.
     */
    void readMail();
}
