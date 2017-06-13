package com.cleanarchitecture.shishkin.api.repository;

import android.arch.lifecycle.Observer;

import com.cleanarchitecture.shishkin.api.controller.ISubscriber;

public interface IObserver<T> extends Observer<T>, ISubscriber {
}
