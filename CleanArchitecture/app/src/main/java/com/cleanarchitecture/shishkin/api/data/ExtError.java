package com.cleanarchitecture.shishkin.api.data;

import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

public class ExtError implements IExtError {
    private StringBuilder mErrorText = new StringBuilder();
    private String mSender = null;

    @Override
    public String getErrorText() {
        return mErrorText.toString();
    }

    @Override
    public ExtError setError(final String sender, final String error) {
        mSender = sender;
        addError(error);
        return this;
    }

    private void addError(final String error) {
        if (!StringUtils.isNullOrEmpty(error)) {
            if (mErrorText.length() > 0) {
                mErrorText.append("\n");
            }
            mErrorText.append(error);
        }
    }

    @Override
    public ExtError setError(final String sender, final Exception e, final String error) {
        if (e != null) {
            ErrorController.getInstance().onError(sender, e);

            mSender = sender;
            addError(error);
        }
        return this;
    }

    @Override
    public ExtError setError(final String sender, final Exception e, final int code) {
        if (e != null) {
            ErrorController.getInstance().onError(sender, e);

            mSender = sender;
            addError(AdminUtils.getErrorText(code));
        }
        return this;
    }

    @Override
    public ExtError setError(final String sender, final int code) {
        if (code != 0) {
            mSender = sender;
            addError(AdminUtils.getErrorText(code));
        }
        return this;
    }

    @Override
    public boolean hasError() {
        return mErrorText.length() > 0;
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
