package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.presenter.IPresenter;

public interface IPresenterController extends IController<IPresenter> {

    /**
     * Получить presenter
     *
     * @param name имя presenter
     * @return presenter
     */
    IPresenter getPresenter(String name);

}
