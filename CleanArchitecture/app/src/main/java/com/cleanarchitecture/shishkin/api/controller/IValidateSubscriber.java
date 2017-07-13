package com.cleanarchitecture.shishkin.api.controller;

import com.cleanarchitecture.shishkin.api.validate.IValidator;

public interface IValidateSubscriber extends ISubscriber {

    IValidator getValidator();

}
