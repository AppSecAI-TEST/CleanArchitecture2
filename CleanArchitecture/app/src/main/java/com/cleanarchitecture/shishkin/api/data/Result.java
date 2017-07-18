package com.cleanarchitecture.shishkin.api.data;

import android.support.annotation.Nullable;

public class Result<T> {

    private T mResult = null;
    private ExtError mError = null;

    public T getResult() {
        return mResult;
    }

    public Result<T> setResult(final T result) {
        mResult = result;
        return this;
    }

    public ExtError getError() {
        return mError;
    }

    public Result<T> setError(final ExtError error) {
        mError = error;
        return this;
    }

    public Result<T> setError(final String sender, final String error) {
        if (mError == null) {
            mError = new ExtError();
        }
        mError.addError(sender, error);
        return this;
    }

    public Result<T> setError(final String sender, final int code) {
        if (mError == null) {
            mError = new ExtError();
        }
        mError.addError(sender, code);
        return this;
    }

    public Result<T> setError(final String sender, final Exception e, final String error) {
        if (mError == null) {
            mError = new ExtError();
        }
        mError.addError(sender, e, error);
        return this;
    }

    public Result<T> setError(final String sender, final Exception e, final int error) {
        if (mError == null) {
            mError = new ExtError();
        }
        mError.addError(sender, e, error);
        return this;
    }

    public boolean hasError() {
        if (mError == null) {
            return false;
        }
        return mError.hasError();
    }

    @Nullable
    public String getErrorText() {
        if (mError != null) {
            return mError.getErrorText();
        }
        return null;
    }

    @Nullable
    public String getSender() {
        if (mError != null) {
            return mError.getSender();
        }
        return null;
    }
}
