package com.cleanarchitecture.shishkin.api.data;

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
        this.mError = error;
        return this;
    }

    public boolean hasError() {
        if (mError == null) {
            return false;
        }
        return mError.hasError();
    }

}
