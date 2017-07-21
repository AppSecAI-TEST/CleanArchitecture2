package com.cleanarchitecture.shishkin.api.presenter;

import android.os.Bundle;

import com.cleanarchitecture.shishkin.api.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.api.ui.IView;
import com.cleanarchitecture.shishkin.common.state.IViewStateListener;

public interface IPresenter<M> extends IViewStateListener, IView, IModuleSubscriber {

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
     * @return true -  презентер готов
     */
    boolean validate();

    /**
     * Получить данные о состоянии презентера
     *
     * @return данные презентера для сохранения и восстановления состояния
     */
    Bundle getStateData();

    /**
     * Флаг - сохранять/стирать состояние при уничтожении презентера
     *
     * @param lostStateData true - сохранять состояние, false - стирать состояние
     */
    void setLostStateData(boolean lostStateData);
}
