package com.cleanarchitecture.shishkin.api.usecases;

import com.cleanarchitecture.shishkin.api.controller.ISubscriber;

public abstract class AbstractUseCase implements ISubscriber {

    public abstract String getName();

}
