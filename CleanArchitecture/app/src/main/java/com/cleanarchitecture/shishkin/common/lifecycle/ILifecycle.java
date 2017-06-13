package com.cleanarchitecture.shishkin.common.lifecycle;

/**
 * Интерфейс объекта, имеющего состояния
 */
public interface ILifecycle extends IStateable {
    /**
     * Событие - объект на этапе создания
     */
    void onCreateLifecycle();

    /**
     * Событие - объект готов к использованию
     */
    void onReadyLifecycle();

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
