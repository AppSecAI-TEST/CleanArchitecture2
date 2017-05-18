package com.cleanarchitecture.shishkin.base.event.ui;


import com.cleanarchitecture.shishkin.base.event.AbstractEvent;

/**
 * Событие - нажата кнопка на SnackBar
 */
public class OnSnackBarClickEvent extends AbstractEvent {
    private String mActionText;

    public OnSnackBarClickEvent(final String text) {
        mActionText = text;
    }

    public String getText() {
        return mActionText;
    }
}
