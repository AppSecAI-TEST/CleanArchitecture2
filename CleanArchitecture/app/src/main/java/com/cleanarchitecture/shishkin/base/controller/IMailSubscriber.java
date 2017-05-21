package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.lifecycle.IState;

public interface IMailSubscriber extends ISubscriber, IState {
    /**
     * Read mail by subscriber.
     */
    void readMail();
}
