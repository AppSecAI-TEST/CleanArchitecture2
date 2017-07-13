package com.cleanarchitecture.shishkin.api.controller;

@SuppressWarnings("unused")
public interface IValidateController extends IController<IValidateSubscriber> {

    boolean validate(IValidateSubscriber subscriber, Object object);

}
