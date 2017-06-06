package com.cleanarchitecture.shishkin.base.repository;

import com.cleanarchitecture.shishkin.base.controller.IModule;
import com.cleanarchitecture.shishkin.base.repository.requests.IRequest;

public interface INetProvider extends IModule {

    void request(IRequest request);

    void setPaused(boolean paused);

}
