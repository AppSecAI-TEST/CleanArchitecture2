package com.cleanarchitecture.shishkin.base.repository;

import com.cleanarchitecture.shishkin.base.controller.ISubscriber;
import com.cleanarchitecture.shishkin.base.repository.requests.IRequest;

public interface INetProvider extends ISubscriber {

    void request(IRequest request);

    void setPaused(boolean paused);

}
