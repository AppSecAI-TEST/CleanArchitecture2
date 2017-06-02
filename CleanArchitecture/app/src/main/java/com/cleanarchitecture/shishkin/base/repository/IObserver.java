package com.cleanarchitecture.shishkin.base.repository;

import com.cleanarchitecture.shishkin.base.controller.ISubscriber;
import android.arch.lifecycle.Observer;

public interface  IObserver<T> extends Observer<T>, ISubscriber {
}
