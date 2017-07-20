package com.cleanarchitecture.shishkin.common.state;

import java.lang.ref.WeakReference;

/**
 * Объект, отвечающий за текущее состояние внешнего объекта
 */
public class ViewStateObserver implements IStateable {
    public static final int STATE_CREATE = 0;
    public static final int STATE_READY = 1;
    public static final int STATE_DESTROY = 2;
    public static final int STATE_PAUSE = 3;
    public static final int STATE_RESUME = 4;

    private int mState = STATE_CREATE;
    private WeakReference<IViewStateListener> mListener;

    public ViewStateObserver(final IViewStateListener listener) {
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
    @Override
    public int getState() {
        return mState;
    }

    /**
     * Установить состояние объекта
     *
     * @param state состояние объекта
     */
    @Override
    public void setState(final int state) {
        mState = state;
        switch (mState) {
            case STATE_CREATE:
                onCreateState();
                break;

            case STATE_READY:
                onViewCreatedState();
                break;

            case STATE_DESTROY:
                onDestroyState();
                break;

            case STATE_PAUSE:
                onPauseState();
                break;

            case STATE_RESUME:
                onResumeState();
                break;

            default:
                break;

        }
    }

    private void onCreateState() {
        if (mListener != null && mListener.get() != null) {
            mListener.get().onCreateState();
        }
    }

    private void onViewCreatedState() {
        if (mListener != null && mListener.get() != null) {
            mListener.get().onReadyState();
        }
    }

    private void onResumeState() {
        if (mListener != null && mListener.get() != null) {
            mListener.get().onResumeState();
        }
    }

    private void onPauseState() {
        if (mListener != null && mListener.get() != null) {
            mListener.get().onPauseState();
        }
    }

    private void onDestroyState() {
        if (mListener != null && mListener.get() != null) {
            mListener.get().onDestroyState();
        }
    }
}
