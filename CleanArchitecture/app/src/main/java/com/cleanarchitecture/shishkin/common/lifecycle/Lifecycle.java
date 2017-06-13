package com.cleanarchitecture.shishkin.common.lifecycle;

import java.lang.ref.WeakReference;

/**
 * Объект, отвечающий за текущее состояние внешнего объекта
 */
public class Lifecycle {
    public static final int STATE_CREATE = 0;
    public static final int STATE_READY = 1;
    public static final int STATE_DESTROY = 2;
    public static final int STATE_PAUSE = 3;
    public static final int STATE_RESUME = 4;

    private int mState = STATE_CREATE;
    private WeakReference<ILifecycle> mListener;

    public Lifecycle(final ILifecycle listener) {
        if (listener != null) {
            mListener = new WeakReference<>(listener);
        }
        setState(STATE_CREATE);
    }

    /**
     * Получить состояние объекта
     *
     * @return состояние объекта
     */
    public int getState() {
        return mState;
    }

    /**
     * Установить состояние объекта
     *
     * @param state состояние объекта
     */
    public void setState(final int state) {
        mState = state;
        switch (mState) {
            case STATE_CREATE:
                onCreateLifecycle();
                break;

            case STATE_READY:
                onViewCreatedLifecycle();
                break;

            case STATE_DESTROY:
                onDestroyLifecycle();
                break;

            case STATE_PAUSE:
                onPauseLifecycle();
                break;

            case STATE_RESUME:
                onResumeLifecycle();
                break;

            default:
                break;

        }
    }

    private void onCreateLifecycle() {
        if (mListener != null && mListener.get() != null) {
            mListener.get().onCreateLifecycle();
        }
    }

    private void onViewCreatedLifecycle() {
        if (mListener != null && mListener.get() != null) {
            mListener.get().onReadyLifecycle();
        }
    }

    private void onResumeLifecycle() {
        if (mListener != null && mListener.get() != null) {
            mListener.get().onResumeLifecycle();
        }
    }

    private void onPauseLifecycle() {
        if (mListener != null && mListener.get() != null) {
            mListener.get().onPauseLifecycle();
        }
    }

    private void onDestroyLifecycle() {
        if (mListener != null && mListener.get() != null) {
            mListener.get().onDestroyLifecycle();
        }
    }
}
