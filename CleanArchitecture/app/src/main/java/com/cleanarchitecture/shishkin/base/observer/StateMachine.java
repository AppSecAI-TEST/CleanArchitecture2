package com.cleanarchitecture.shishkin.base.observer;

import com.cleanarchitecture.shishkin.base.lifecycle.IStateable;
import com.cleanarchitecture.shishkin.base.lifecycle.Lifecycle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StateMachine {
    private List<WeakReference<IStateable>> mList = Collections.synchronizedList(new ArrayList<>());
    private int mState = Lifecycle.STATE_CREATE;

    public StateMachine(final int state) {
        setState(state);
    }

    public synchronized StateMachine setState(final int state) {
        mState = state;
        for (WeakReference<IStateable> stateable : mList) {
            if (stateable != null && stateable.get() != null) {
                stateable.get().setState(mState);
            }
        }
        return this;
    }

    public synchronized int getState() {
        return mState;
    }

    public synchronized void addObserver(final IStateable stateable) {
        if (stateable != null) {
            for (WeakReference<IStateable> ref : mList) {
                if (ref != null && ref.get() != null) {
                    if (ref.get() == stateable) {
                        return;
                    }
                }
            }

            stateable.setState(mState);
            mList.add(new WeakReference<>(stateable));
        }
    }

    public synchronized void removeObserver(final IStateable stateable) {
        for (WeakReference<IStateable> ref : mList) {
            if (ref != null && ref.get() != null) {
                if (ref.get() == stateable) {
                    mList.remove(ref);
                    return;
                }
            }
        }
    }

    public synchronized void clear() {
        mList.clear();
    }

}
