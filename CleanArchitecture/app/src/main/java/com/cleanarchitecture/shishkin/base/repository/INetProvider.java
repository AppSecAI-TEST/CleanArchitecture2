package com.cleanarchitecture.shishkin.base.repository;

import com.cleanarchitecture.shishkin.base.repository.net.requests.AbstractRequest;

public interface INetProvider {
    void request(final AbstractRequest request);

    void setPaused(final boolean paused);

}
