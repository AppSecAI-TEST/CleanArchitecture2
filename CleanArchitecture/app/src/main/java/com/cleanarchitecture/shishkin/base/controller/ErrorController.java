package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.event.ui.ShowErrorMessageEvent;
import com.github.snowdream.android.util.Log;

/**
 * Контроллер ошибок
 */
public class ErrorController extends AbstractController {
    public static final String NAME = "ErrorController";
    private static volatile ErrorController sInstance;

    public static final int ERROR_LOST_AAPLICATION_CONTEXT = 1;
    public static final int ERROR_GET_DATA = 2;
    public static final int ERROR_DB = 3;


    public static ErrorController getInstance() {
        if (sInstance == null) {
            synchronized (ErrorController.class) {
                if (sInstance == null) {
                    sInstance = new ErrorController();
                }
            }
        }
        return sInstance;
    }

    private ErrorController() {
    }

    public synchronized void onError(final String source, final Exception e) {
        Log.e(source, e.getMessage());
    }

    public synchronized void onError(final String source, final Throwable throwable) {
        Log.e(source, throwable.getMessage());
    }

    public synchronized void onError(final String source, final Exception e, final String displayMessage) {
        onError(source, e);

        EventBusController.getInstance().post(new ShowErrorMessageEvent(displayMessage));
    }

    public synchronized void onError(final String source, final Exception e, final int errorCode) {
        onError(source, e);

        EventBusController.getInstance().post(new ShowErrorMessageEvent(errorCode));
    }
    public synchronized void onError(final String source, final String displayMessage) {
        EventBusController.getInstance().post(new ShowErrorMessageEvent(displayMessage));
    }

    public synchronized void onError(final String source, final int errorCode) {
        EventBusController.getInstance().post(new ShowErrorMessageEvent(errorCode));
    }

    @Override
    public String getName() {
        return NAME;
    }

}
