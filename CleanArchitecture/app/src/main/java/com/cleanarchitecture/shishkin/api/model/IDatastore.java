package com.cleanarchitecture.shishkin.api.model;

import com.cleanarchitecture.shishkin.api.controller.IModuleSubscriber;

public interface IDatastore<T extends ILiveData> extends IModuleSubscriber {

    /**
     * Получить ILiveData
     *
     * @return ILiveData
     */
    T getLiveData();

    /**
     * Получить данные и поместить их в ILiveData
     */
    void getData();

    /**
     * Остановить получение данных
     */
    void terminate();

    /**
     * Очистить данные в ILiveData
     */
    void clearData();

    /**
     * Событие - данные изменились
     */
    void onChangeData();
}
