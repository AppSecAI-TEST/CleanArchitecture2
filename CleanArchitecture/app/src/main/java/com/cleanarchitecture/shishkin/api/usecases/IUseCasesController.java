package com.cleanarchitecture.shishkin.api.usecases;

import com.cleanarchitecture.shishkin.api.controller.IModule;

/**
 * The interface Use cases controller.
 */
public interface IUseCasesController extends IModule {

    /**
     * Флаг - приложение остановлено
     *
     * @return true = приложение остановлено
     */
    boolean isApplicationFinished();
}
