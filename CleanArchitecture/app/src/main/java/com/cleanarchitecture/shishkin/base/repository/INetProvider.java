package com.cleanarchitecture.shishkin.base.repository;

import com.cleanarchitecture.shishkin.base.repository.requests.IRequest;

public interface INetProvider {

    void request(IRequest request);

    void setPaused(boolean paused);

}
