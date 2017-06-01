package com.cleanarchitecture.shishkin.base.event.ui;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.event.AbstractEvent;
import com.cleanarchitecture.shishkin.base.event.IEvent;

/**
 * Событие - выполнить команду "показать сообщение об ошибке"
 */
public class ShowErrorMessageEvent extends AbstractEvent {
    private String mMessage;

    public ShowErrorMessageEvent(final IEvent event) {
        if (getErrorCode() == 1) {
            final Context context = ApplicationController.getInstance();
            if (context != null) {
                mMessage = context.getString(R.string.error_db_app_not_loaded);
            } else {
                mMessage = "Application not loaded";
            }
        } else {
            mMessage = event.getErrorText();
        }
    }

    public String getMessage() {
        return mMessage;
    }

}
