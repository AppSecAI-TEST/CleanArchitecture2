package com.cleanarchitecture.shishkin.api.observer;

import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("unused")
public abstract class AbstractObserver implements Observer {

    private Observable mObservable;

    public AbstractObserver(final Observable observable) {
        if (observable != null) {
            mObservable = observable;
            mObservable.addObserver(this);
        }
    }

    @Override
    public void update(final Observable o, final Object arg) {
    }

    public void finish() {
        if (mObservable != null) {
            mObservable.deleteObserver(this);
        }
    }

}
