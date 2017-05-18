package com.cleanarchitecture.shishkin.base.lifecycle;

/**
 * Интерфейс объекта, имеющего состояния
 */
public interface ILifecycle extends IState {
    /**
     * Получить состояние объекта
     *
     * @return the state
     */
    int getState();

    /**
     * Событие - объект на этапе создания
     */
    void onCreateLifecycle();

    /**
     * Событие - объект уже создан и готов к использованию
     */
    void onViewCreatedLifecycle();

    /**
     * Событие - объект становиться видимым на экране
     */
    void onResumeLifecycle();

    /**
     * Событие - объект уходит в фон
     */
    void onPauseLifecycle();

    /**
     * Событие - уничтожение объекта
     */
    void onDestroyLifecycle();

}
