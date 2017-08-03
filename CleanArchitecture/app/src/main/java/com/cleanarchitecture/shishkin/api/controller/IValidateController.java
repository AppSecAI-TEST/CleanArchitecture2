package com.cleanarchitecture.shishkin.api.controller;

import com.cleanarchitecture.shishkin.api.data.Result;

@SuppressWarnings("unused")
public interface IValidateController extends IController<IValidateSubscriber> {

    Result<Boolean> validate(String name, Object object);

}
