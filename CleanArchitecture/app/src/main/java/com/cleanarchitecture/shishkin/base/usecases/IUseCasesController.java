package com.cleanarchitecture.shishkin.base.usecases;

import com.cleanarchitecture.shishkin.base.controller.ISubscriber;

/**
 * The interface Use cases controller.
 */
public interface IUseCasesController extends ISubscriber {

    /**
     * Установить флаг - выводиться системный диалог
     *
     * @param shown true - на экран выводиться системный диалог
     */
    void setSystemDialogShown(boolean shown);

    /**
     * Флаг - выводиться системный диалог
     *
     * @return true - на экран выводиться системный диалог
     */
    boolean isSystemDialogShown();
}
