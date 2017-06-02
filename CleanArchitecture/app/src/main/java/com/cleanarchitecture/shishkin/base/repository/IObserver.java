package com.cleanarchitecture.shishkin.base.repository;

import android.arch.lifecycle.Observer;

import com.cleanarchitecture.shishkin.base.controller.ISubscriber;

public interface IObserver<T> extends Observer<T>, ISubscriber {
}
