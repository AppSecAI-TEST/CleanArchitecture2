package com.cleanarchitecture.shishkin.base.presenter;

import com.cleanarchitecture.shishkin.base.lifecycle.ILifecycle;

public interface IPresenter<M> extends ILifecycle {

    /**
     * Получить имя презентера
     *
     * @return имя презентера
     */
    String getName();

    /**
     * Установить модель презентера
     *
     * @param model the model
     */
    void setModel(M model);

    /**
     * Получить модель презентера
     *
     * @return the model
     */
    M getModel();

    /**
     * Обновить Views презентера
     */
    void updateView();

    /**
     * Флаг - регистрировать презентер в контроллере презентеров
     *
     * @return the boolean
     */
    boolean isRegister();

    /**
     * Проверить презентер
     *
     * @return the boolean
     */
    boolean validate();

    /**
     * Показать progress bar.
     */
    void showProgressBar();

    /**
     * Скрыть progress bar.
     */
    void hideProgressBar();


}
