package com.cleanarchitecture.shishkin.api.repository.data;

import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

public class ExtError implements IExtError {
    private String mErrorText = null;
    private int mErrorCode = 0;
    private String mSender = null;

    @Override
    public String getErrorText() {
        return mErrorText;
    }

    @Override
    public ExtError setErrorText(final String sender, String errorText) {
        if (!StringUtils.isNullOrEmpty(errorText)) {
            mSender = sender;
            mErrorText = errorText;
            ErrorController.getInstance().onError(sender, errorText, true);
        }
        return this;
    }

    @Override
    public ExtError setErrorText(final String sender, final Exception e, final String error) {
        if (e != null) {
            mSender = sender;
            mErrorText = error;
            ErrorController.getInstance().onError(sender, e, error);
        }
        return this;
    }

    @Override
    public ExtError setErrorCode(final String sender, final int code) {
        if (code != 0) {
            mSender = sender;
            mErrorCode = code;
            ErrorController.getInstance().onError(sender, code, true);
        }
        return this;
    }

    @Override
    public int getErrorCode() {
        return mErrorCode;
    }

    @Override
    public boolean hasError() {
        return !(StringUtils.isNullOrEmpty(mErrorText) && mErrorCode == 0);
    }

    @Override
    public String getSender() {
        return mSender;
    }

    @Override
    public ExtError setSender(final String sender) {
        mSender = sender;
        return this;
    }


}
