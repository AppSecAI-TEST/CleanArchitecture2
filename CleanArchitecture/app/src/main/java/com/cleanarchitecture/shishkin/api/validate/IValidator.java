package com.cleanarchitecture.shishkin.api.validate;

import com.cleanarchitecture.shishkin.api.controller.ISubscriber;

public interface IValidator extends ISubscriber {

    boolean validate(Object object);

    Object fix(Object object);

    void add(IValidator validator);

    boolean execValidate(Object object);

}
