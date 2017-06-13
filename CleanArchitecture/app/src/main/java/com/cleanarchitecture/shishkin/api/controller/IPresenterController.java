package com.cleanarchitecture.shishkin.api.controller;

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

}
