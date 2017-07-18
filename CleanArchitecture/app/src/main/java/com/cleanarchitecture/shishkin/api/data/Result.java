package com.cleanarchitecture.shishkin.api.data;

public class Result<T> {

    private T mResult = null;
    private ExtError mError = null;

    public T getResult() {
        return mResult;
    }

    public Result setResult(final T result) {
        mResult = result;
        return this;
    }

    public ExtError getError() {
        return mError;
    }

    public void setError(final ExtError error) {
        this.mError = error;
    }

    public boolean hasError() {
        if (mError == null) {
            return false;
        }
        return mError.hasError();
    }

}
