package com.cleanarchitecture.shishkin.base.lifecycle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * State machine.
 */
public class StateMachine implements IStateable{
    private List<WeakReference<IStateable>> mList = Collections.synchronizedList(new ArrayList<>());
    private int mState = Lifecycle.STATE_CREATE;

    public StateMachine(final int state) {
        setState(state);
    }

    /**
     * Установить текущее состояние объекта
     *
     * @param state текущее состояние объекта
     */
    @Override
    public synchronized void setState(final int state) {
        mState = state;
        for (WeakReference<IStateable> stateable : mList) {
            if (stateable != null && stateable.get() != null) {
                stateable.get().setState(mState);
            }
        }
    }

    /**
     * Получить текущее состояние объекта
     *
     * @return текущее состояние объекта
     */
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
