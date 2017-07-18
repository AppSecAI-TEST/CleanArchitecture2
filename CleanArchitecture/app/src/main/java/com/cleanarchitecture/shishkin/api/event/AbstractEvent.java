package com.cleanarchitecture.shishkin.api.event;

import com.cleanarchitecture.shishkin.api.data.ExtError;

public abstract class AbstractEvent implements IEvent {

    private int mId = -1;
    private ExtError mError;
    private String mSender = null;

    @Override
    public ExtError getError() {
        return mError;
    }

    @Override
    public IEvent setError(final ExtError error) {
        this.mError = error;
        return this;
    }

    @Override
    public boolean hasError() {
        if (mError == null) {
            return false;
        }
        return mError.hasError();
    }

    @Override
    public int getId() {
        return mId;
    }

    @Override
    public IEvent setId(final int id) {
        mId = id;
        return this;
    }

    @Override
    public String getSender() {
        return mSender;
    }

    @Override
    public IEvent setSender(final String sender) {
        mSender = sender;
        return this;
    }

}
