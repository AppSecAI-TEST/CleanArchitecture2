package com.cleanarchitecture.shishkin.api.event.ui;

import android.os.Bundle;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

/**
 * Событие - диалог завершен, с указанным результатом
 */
public class DialogResultEvent extends AbstractEvent {

    private Bundle mResult;

    public DialogResultEvent(final Bundle result) {
        mResult = result;
    }

    public Bundle getResult() {
        return mResult;
    }

}
