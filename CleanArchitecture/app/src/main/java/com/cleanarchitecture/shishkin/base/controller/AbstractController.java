package com.cleanarchitecture.shishkin.base.controller;

/**
 * Абстрактный контроллер
 */
public abstract class AbstractController implements ISubscriber {

    @Override
    public abstract String getName();

}
