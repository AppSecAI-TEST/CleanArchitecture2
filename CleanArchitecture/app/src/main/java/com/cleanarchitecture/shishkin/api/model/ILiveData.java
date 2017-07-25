package com.cleanarchitecture.shishkin.api.model;

public interface ILiveData<T> {

    T getValue();

    void setValue(T object);

    void postValue(T object);

}
