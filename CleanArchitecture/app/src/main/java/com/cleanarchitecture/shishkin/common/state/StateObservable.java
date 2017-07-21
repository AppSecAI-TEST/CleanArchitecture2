package com.cleanarchitecture.shishkin.common.state;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StateObservable implements IStateable {
    private List<WeakReference<IStateable>> mList = Collections.synchronizedList(new ArrayList<>());
    private int mState = ViewStateObserver.STATE_CREATE;

    public StateObservable(final int state) {
        setState(state);
    }

    @Override
    public synchronized void setState(final int state) {
        mState = state;
        for (WeakReference<IStateable> stateable : mList) {
            if (stateable != null && stateable.get() != null) {
                stateable.get().setState(mState);
            }
        }
    }

    public synchronized int getState() {
        return mState;
    }

    /**
     * Добавить слушателя состояний
     *
     * @param stateable слушатель состояний
     */
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

    /**
     * Удалить слушателя состояний
     *
     * @param stateable слушатель состояний
     */
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

    /**
     * Удалить всех слушателей
     */
    public synchronized void clear() {
        mList.clear();
    }

}
