package com.cleanarchitecture.shishkin.api.validate;

import com.cleanarchitecture.shishkin.api.controller.ISubscriber;
import com.cleanarchitecture.shishkin.api.data.Result;

public interface IValidator<T> extends ISubscriber {

    Result<Boolean> validate(T object);

    T fix(T object);

    void add(IValidator validator);

    IValidator get(String name);

}
