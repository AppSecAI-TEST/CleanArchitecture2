package com.cleanarchitecture.shishkin.base.event;

import com.cleanarchitecture.shishkin.base.utils.StringUtils;

import java.lang.ref.WeakReference;

public abstract class AbstractEvent implements IEvent{

    private String mErrorText = null;
    private int mErrorCode = 0;
    private int mId = -1;
    private WeakReference<Object> mSender = null;

    @Override
    public String getErrorText() {
        return mErrorText;
    }

    @Override
    public IEvent setErrorText(final String error) {
        this.mErrorText = error;
        return this;
    }

    @Override
    public int getErrorCode() {
        return mErrorCode;
    }

    @Override
    public IEvent setErrorCode(final int code) {
        this.mErrorCode = code;
        return this;
    }

    @Override
    public boolean hasError() {
        return !(StringUtils.isNullOrEmpty(mErrorText) && mErrorCode == 0);
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
    public Object getSender() {
        if (mSender != null && mSender.get() != null) {
            return mSender.get();
        }
        return null;
    }

    @Override
    public IEvent setSender(final Object sender) {
        if (sender != null) {
            mSender = new WeakReference<>(sender);
        }
        return this;
    }

}
