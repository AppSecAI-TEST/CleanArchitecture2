package com.cleanarchitecture.shishkin.api.controller;

import java.util.List;

public interface IValidateSubscriber extends ISubscriber {

    /**
     * Список имен валидаторов, которые хочет использовать объект
     *
     * @return список имен валидаторов
     */
    List<String> hasValidatorType();

}
