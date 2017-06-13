package com.cleanarchitecture.shishkin.api.task;

import com.cleanarchitecture.shishkin.api.repository.requests.IRequest;

public interface IPhonePausableThreadPoolExecutor {

    void execute(final IRequest request);

    void setPaused(final boolean paused);

}
