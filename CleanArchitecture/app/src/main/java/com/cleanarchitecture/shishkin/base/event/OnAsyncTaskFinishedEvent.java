package com.cleanarchitecture.shishkin.base.event;

/**
 * Событие - AsyncTask завершилось
 */
public class OnAsyncTaskFinishedEvent extends AbstractEvent {

    private String mId;

    public OnAsyncTaskFinishedEvent(final String id) {
        mId = id;
    }

    public String getUUID() {
        return mId;
    }
}
