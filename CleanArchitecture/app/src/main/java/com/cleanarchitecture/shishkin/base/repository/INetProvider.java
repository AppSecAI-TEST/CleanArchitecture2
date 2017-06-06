package com.cleanarchitecture.shishkin.base.repository;

import com.cleanarchitecture.shishkin.base.controller.IModule;
import com.cleanarchitecture.shishkin.base.repository.requests.IRequest;

public interface INetProvider extends IModule {

    /**
     * Выполнить запрос
     *
     * @param request the request
     */
    void request(IRequest request);

    /**
     * Установить состояние паузы
     *
     * @param paused true - остановить, false - продолжить
     */
    void setPaused(boolean paused);

}
