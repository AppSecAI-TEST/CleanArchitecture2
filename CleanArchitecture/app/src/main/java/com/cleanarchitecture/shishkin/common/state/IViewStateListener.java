package com.cleanarchitecture.shishkin.common.state;

/**
 * Интерфейс слушателя View объекта, имеющего состояния
 */
public interface IViewStateListener extends IStateable {
    /**
     * Событие - объект на этапе создания
     */
    void onCreateState();

    /**
     * Событие - объект готов к использованию
     */
    void onReadyState();

    /**
     * Событие - объект становиться видимым на экране
     */
    void onResumeState();

    /**
     * Событие - объект уходит в фон
     */
    void onPauseState();

    /**
     * Событие - уничтожение объекта
     */
    void onDestroyState();

}
