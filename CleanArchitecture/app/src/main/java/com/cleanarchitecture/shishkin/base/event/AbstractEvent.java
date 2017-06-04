package com.cleanarchitecture.shishkin.base.event;

import com.cleanarchitecture.shishkin.base.controller.ErrorController;
import com.cleanarchitecture.shishkin.base.utils.StringUtils;

import java.lang.ref.WeakReference;

public abstract class AbstractEvent implements IEvent {

    private String mErrorText = null;
    private int mErrorCode = 0;
    private int mId = -1;
    private String mSender = null;

    @Override
    public String getErrorText() {
        return mErrorText;
    }

    @Override
    public IEvent setErrorText(final String sender, final String error) {
        mSender = sender;
        mErrorText = error;
        ErrorController.getInstance().onError(sender, error);
        return this;
    }

    @Override
    public IEvent setErrorText(final String sender, final Exception e, final String error) {
        mSender = sender;
        mErrorText = error;
        ErrorController.getInstance().onError(sender, e, error);
        return this;
    }

    @Override
    public int getErrorCode() {
        return mErrorCode;
    }

    @Override
    public IEvent setErrorCode(final String sender, final int code) {
        mSender = sender;
        mErrorCode = code;
        ErrorController.getInstance().onError(sender, code);
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
    public String getSender() {
        return mSender;
    }

    @Override
    public IEvent setSender(final String sender) {
        mSender = sender;
        return this;
    }

}
