package com.cleanarchitecture.shishkin.api.model;

public interface ILiveData<T> {

    /**
     * Получить данные из LiveData
     *
     * @return данные
     */
    T getValue();

    /**
     * Установить данные в LiveData немедленно (выполняется в UI потоке)
     *
     * @param object данные
     */
    void setValue(T object);

    /**
     * Установить данные в LiveData
     *
     * @param object данные
     */
    void postValue(T object);

}
