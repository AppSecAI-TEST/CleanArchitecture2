package com.cleanarchitecture.shishkin.base.usecases;

import com.cleanarchitecture.shishkin.base.controller.ISubscriber;

public abstract class AbstractUseCase implements ISubscriber {

    public abstract String getName();

}
