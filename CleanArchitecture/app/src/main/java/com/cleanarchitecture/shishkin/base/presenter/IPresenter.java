package com.cleanarchitecture.shishkin.base.presenter;

import com.cleanarchitecture.shishkin.base.lifecycle.ILifecycle;
import com.cleanarchitecture.shishkin.base.ui.IView;

public interface IPresenter<M> extends ILifecycle, IView {

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

}
