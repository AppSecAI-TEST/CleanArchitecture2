package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.ui.activity.IActivity;

import java.lang.ref.WeakReference;
import java.util.Map;

public interface IController<T> extends ISubscriber {

    void register(T subscriber);

    void unregister(T subscriber);

    void setCurrentSubscriber(T subscriber);

    T getCurrentSubscriber();

    Map<String, WeakReference<T>> getSubscribers();

    T getSubscriber();
}
