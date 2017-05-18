package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.presenter.IPresenter;

public interface IPresenterController {

    /**
     * Зарегистрировать presenter
     *
     * @param presenter presenter
     */
    void register(IPresenter presenter);

    /**
     * Отключить presenter
     *
     * @param presenter presenter
     */
    void unregister(IPresenter presenter);

    /**
     * Получить presenter
     *
     * @param name имя presenter
     * @return presenter
     */
    IPresenter getPresenter(String name);

}
