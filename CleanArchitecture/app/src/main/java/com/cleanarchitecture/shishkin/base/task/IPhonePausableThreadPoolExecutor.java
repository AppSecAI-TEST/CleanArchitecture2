package com.cleanarchitecture.shishkin.base.task;

import com.cleanarchitecture.shishkin.base.repository.requests.IRequest;

public interface IPhonePausableThreadPoolExecutor {

    void execute(final IRequest request);

    void setPaused(final boolean paused);

}
