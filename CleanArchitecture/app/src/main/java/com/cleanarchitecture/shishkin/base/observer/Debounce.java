package com.cleanarchitecture.shishkin.base.observer;

import android.os.Handler;

import java.lang.ref.WeakReference;

/**
 * Класс, устраняющий дребезг (частое повторение) события
 */
public class Debounce implements Runnable {

    private long mDelay = 5000; //5 sec
    private int mSkip = 0;
    private Handler mHandler = null;
    private WeakReference<Object> mObject;

    /**
     * Конструктор
     *
     * @param delay задержка, после которой запустится действие
     */
    public Debounce(final long delay) {
        this(delay, 0);
    }

    /**
     * Конструктор
     *
     * @param delay задержка, после которой запустится действие
     * @param skip  количество событий, которое будет пропущено перед запуском задержки
     */
    public Debounce(final long delay, final int skip) {
        mHandler = new Handler();
        mDelay = delay;
        mSkip = skip;
    }

    /**
     * Событие
     *
     * @param object объект события
     */
    public void onEvent(final Object object) {
        if (object != null) {
            mObject = new WeakReference<>(object);
        } else {
            mObject = null;
        }

        if (mSkip >= 0) {
            mSkip--;
        }

        if (mSkip < 0) {
            mHandler.removeCallbacks(this);
            mHandler.postDelayed(this, mDelay);
        }
    }

    @Override
    public void run() {
    }

    /**
     * остановить объект
     */
    public void finish() {
        mObject = null;

        mHandler.removeCallbacks(this);
        mHandler = null;
    }

    /**
     * Получить объект события
     *
     * @return the object
     */
    public Object getObject() {
        if (mObject != null) {
            return mObject.get();
        }
        return null;
    }

}
