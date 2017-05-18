package com.cleanarchitecture.shishkin.base.event.ui;

import android.os.Bundle;

import com.cleanarchitecture.shishkin.base.event.AbstractEvent;

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
