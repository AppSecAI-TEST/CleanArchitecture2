package com.cleanarchitecture.shishkin.api.controller;

import android.os.Bundle;

import com.cleanarchitecture.shishkin.api.presenter.IPresenter;

@SuppressWarnings("unused")
public interface IPresenterController extends IController<IPresenter> {

    /**
     * Получить presenter
     *
     * @param name имя presenter
     * @return presenter
     */
    IPresenter getPresenter(String name);

    /**
     * Сохранить состояние presenter
     *
     * @param name  имя presenter
     * @param state состояние
     */
    void saveState(String name, Bundle state);

    /**
     * Получить состояние presenter
     *
     * @param name имя presenter
     * @return состояние presenter
     */
    Bundle restoreState(String name);

    /**
     * Очистить состояние presenter
     *
     * @param name имя presenter
     */
    void clearState(String name);

}
