package com.cleanarchitecture.shishkin.common.lifecycle;

import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.IPresenterController;
import com.cleanarchitecture.shishkin.api.presenter.IPresenter;
import com.cleanarchitecture.shishkin.common.utils.SafeUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * State machine.
 */
public class StateMachine implements IStateable {
    private List<WeakReference<IStateable>> mList = Collections.synchronizedList(new ArrayList<>());
    private int mState = Lifecycle.STATE_CREATE;
    private boolean mLostStateData = false;

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
                if (mState == Lifecycle.STATE_DESTROY) {
                    saveOrClearStateData(stateable.get());
                }
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

    /**
     * Сохранить/стереть данные состояний всех слушателей
     */
    public synchronized void saveStateData() {
        final IPresenterController controller = AdminUtils.getPresenterController();
        if (controller != null) {
            if (!mLostStateData) {
                for (WeakReference<IStateable> ref : mList) {
                    if (ref != null && ref.get() != null && ref.get() instanceof IPresenter) {
                        final IPresenter presenter = SafeUtils.cast(ref.get());
                        controller.saveStateData(presenter.getName(), presenter.getStateData());
                    }
                }
            } else {
                for (WeakReference<IStateable> ref : mList) {
                    if (ref != null && ref.get() != null && ref.get() instanceof IPresenter) {
                        final IPresenter presenter = SafeUtils.cast(ref.get());
                        controller.clearStateData(presenter.getName());
                    }
                }
            }
        }
    }

    /**
     * Сохранить/стереть данные состояния слушателя
     */
    public synchronized void saveOrClearStateData(final IStateable stateable) {
        final IPresenterController controller = AdminUtils.getPresenterController();
        if (controller != null && stateable != null) {
            if (stateable instanceof IPresenter) {
                final IPresenter presenter = SafeUtils.cast(stateable);
                if (!mLostStateData) {
                    controller.saveStateData(presenter.getName(), presenter.getStateData());
                } else {
                    controller.clearStateData(presenter.getName());
                }
            }
        }
    }

    public void setLostStateDate(boolean lostStateDate) {
        mLostStateData = lostStateDate;
    }

}
