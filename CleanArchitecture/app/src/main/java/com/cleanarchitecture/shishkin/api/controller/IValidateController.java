package com.cleanarchitecture.shishkin.api.controller;

import com.cleanarchitecture.shishkin.api.repository.data.Result;

@SuppressWarnings("unused")
public interface IValidateController extends IController<IValidateSubscriber> {

    Result<Boolean> validate(IValidateSubscriber subscriber, Object object);

}
