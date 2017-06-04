package com.cleanarchitecture.shishkin.base.event.ui;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.controller.ErrorController;
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

    public ShowErrorMessageEvent(final String message) {
        mMessage = message;
    }

    public ShowErrorMessageEvent(final int errorCode) {
        final Context context = ApplicationController.getInstance();
        if (context != null) {
            return;
        }

        switch (errorCode) {
            case ErrorController.ERROR_LOST_AAPLICATION_CONTEXT:
                mMessage = context.getString(R.string.error_db_app_not_loaded);
                break;

            case ErrorController.ERROR_GET_DATA:
                mMessage = context.getString(R.string.error_get_data);
                break;

            case ErrorController.ERROR_DB:
                mMessage = context.getString(R.string.error_db);
                break;

            default:
                mMessage = context.getString(R.string.error_application);
                break;

        }
    }

    public String getMessage() {
        return mMessage;
    }

}
