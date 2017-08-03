package com.cleanarchitecture.shishkin.api.controller;

import com.cleanarchitecture.shishkin.api.data.Result;

@SuppressWarnings("unused")
public interface IValidateController extends IController<IValidateSubscriber> {

    /**
     * проверить объект
     *
     * @param validatorName имя валидатора
     * @param object объект валидации
     * @return результат проверки
     */
    Result<Boolean> validate(String validatorName, Object object);
}
