package com.cleanarchitecture.shishkin.api.event.ui;


import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

/**
 * Событие - выполнить команду "показать CircleProgressBar"
 */
public class ShowCircleProgressBarEvent extends AbstractEvent {

    private float mPosition = 0;

    public ShowCircleProgressBarEvent(final float position) {
        mPosition = position;
    }

    public float getPosition() {
        return mPosition;
    }

}
