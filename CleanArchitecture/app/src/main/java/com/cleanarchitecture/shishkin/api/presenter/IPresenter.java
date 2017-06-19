package com.cleanarchitecture.shishkin.api.presenter;

import com.cleanarchitecture.shishkin.api.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.api.ui.IView;
import com.cleanarchitecture.shishkin.common.lifecycle.ILifecycle;

public interface IPresenter<M> extends ILifecycle, IView, IModuleSubscriber {

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
     * @return true - регистрировать (презентер - глобальный)
     */
    boolean isRegister();

    /**
     * Проверить презентер
     *
     * @return the boolean
     */
    boolean validate();

}
