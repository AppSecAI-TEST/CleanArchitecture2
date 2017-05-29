package com.cleanarchitecture.shishkin.base.task;

import com.cleanarchitecture.shishkin.base.repository.requests.AbstractRequest;

public interface IPhonePausableThreadPoolExecutor {

    void execute(final AbstractRequest request);

    void setPaused(final boolean paused);

}
