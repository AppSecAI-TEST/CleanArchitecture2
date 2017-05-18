package com.cleanarchitecture.shishkin.base.event;

public interface IEvent {

    Object getSender();

    IEvent setSender(Object object);

    String getErrorText();

    IEvent setErrorText(String error);

    int getErrorCode();

    IEvent setErrorCode(int code);

    boolean hasError();

    int getId();

    void setId(int id);

}
