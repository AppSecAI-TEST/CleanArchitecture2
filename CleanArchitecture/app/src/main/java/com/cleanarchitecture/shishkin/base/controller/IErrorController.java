package com.cleanarchitecture.shishkin.base.controller;

public interface IErrorController extends ISubscriber {

    void onError(String source, Exception e);

    void onError(String source, Exception e, String displayMessage);

    void onError(String source, Exception e, int errorCode);

    void onError(String source, int errorCode);

    void onError(String source, String displayMessage);
}
