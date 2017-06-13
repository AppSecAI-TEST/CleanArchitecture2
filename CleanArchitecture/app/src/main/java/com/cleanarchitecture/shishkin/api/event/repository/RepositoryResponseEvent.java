package com.cleanarchitecture.shishkin.api.event.repository;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

public class RepositoryResponseEvent<T> extends AbstractEvent {

    private T mResponse;
    private int mFrom = -1;

    public RepositoryResponseEvent() {
    }

    public T getResponse() {
        return mResponse;
    }

    public RepositoryResponseEvent setResponse(T response) {
        this.mResponse = response;
        return this;
    }

    public int getFrom() {
        return mFrom;
    }

    public RepositoryResponseEvent setFrom(int from) {
        this.mFrom = from;
        return this;
    }


}
