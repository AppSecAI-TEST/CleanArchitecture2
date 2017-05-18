package com.cleanarchitecture.shishkin.base.repository;

import com.cleanarchitecture.shishkin.base.controller.ISubscriber;

import java.io.Serializable;

public interface IRepository extends ISubscriber {

    Serializable getFromCache(int key);

    void putToCache(int key, Serializable value);

    void setDefaultCaching(int defaultCaching);

}
