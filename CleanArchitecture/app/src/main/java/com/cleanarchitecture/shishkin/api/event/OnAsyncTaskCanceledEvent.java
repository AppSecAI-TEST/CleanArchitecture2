package com.cleanarchitecture.shishkin.api.event;

/**
 * Событие - аварийное завершение AsyncTask
 */
public class OnAsyncTaskCanceledEvent extends AbstractEvent {
    private String mId;

    public OnAsyncTaskCanceledEvent(final String id) {
        mId = id;
    }

    public String getUUID() {
        return mId;
    }
}
