package com.cleanarchitecture.shishkin.base.lifecycle;

public interface IStateable {

    /**
     * Получить состояние объекта
     *
     * @return the state
     */
    int getState();

    /**
     * Установить состояние объекта
     *
     * @param state the state
     */
    void setState(int state);

}
