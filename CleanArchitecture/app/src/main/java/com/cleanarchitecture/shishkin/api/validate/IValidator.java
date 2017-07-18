package com.cleanarchitecture.shishkin.api.validate;

import com.cleanarchitecture.shishkin.api.controller.ISubscriber;
import com.cleanarchitecture.shishkin.api.data.Result;

public interface IValidator extends ISubscriber {

    Result<Boolean> validate(Object object);

    Object fix(Object object);

    void add(IValidator validator);

    Result<Boolean> execValidate(Object object);

}
