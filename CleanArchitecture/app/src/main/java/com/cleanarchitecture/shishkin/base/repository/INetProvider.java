package com.cleanarchitecture.shishkin.base.repository;

import com.cleanarchitecture.shishkin.base.repository.requests.AbstractRequest;

public interface INetProvider {

    void request(AbstractRequest request);

    void setPaused(boolean paused);

}
