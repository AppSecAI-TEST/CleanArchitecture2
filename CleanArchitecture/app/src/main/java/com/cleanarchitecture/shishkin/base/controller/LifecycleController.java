package com.cleanarchitecture.shishkin.base.controller;

import android.content.Context;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.event.StartActivityEvent;
import com.cleanarchitecture.shishkin.base.presenter.ActivityPresenter;
import com.cleanarchitecture.shishkin.base.ui.activity.AbstractActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер, отвечающий за жизненный цикл activities приложения
 */
@SuppressWarnings("unused")
public class LifecycleController extends AbstractController
        implements ILifecycleController {

    private static final String NAME = "LifecycleController";
    private Map<String, WeakReference<ILifecycleSubscriber>> mSubscribers = Collections.synchronizedMap(new HashMap<String, WeakReference<ILifecycleSubscriber>>());
    private static volatile LifecycleController sInstance;
    private WeakReference<ILifecycleSubscriber> mCurrentSubscriber;

    public static synchronized void instantiate() {
        if (sInstance == null) {
            synchronized (LifecycleController.class) {
                if (sInstance == null) {
                    sInstance = new LifecycleController();
                }
            }
        }
    }

    public static LifecycleController getInstance() {
        instantiate();
        return sInstance;
    }

    private LifecycleController() {
        mSubscribers = Collections.synchronizedMap(new HashMap<String, WeakReference<ILifecycleSubscriber>>());

        EventController.getInstance().register(this);
    }

    /**
     * Зарегестрировать подписчика
     *
     * @param subscriber подписчик
     */
    @Override
    public synchronized void register(final ILifecycleSubscriber subscriber) {
        if (subscriber != null) {
            checkNullSubscriber();

            mSubscribers.put(subscriber.getName(), new WeakReference<>(subscriber));
        }
    }

    /**
     * Отключить подписчика
     *
     * @param subscriber подписчик
     */
    @Override
    public synchronized void unregister(final ILifecycleSubscriber subscriber) {
        if (subscriber != null) {
            if (mCurrentSubscriber != null && mCurrentSubscriber.get() != null) {
                if (subscriber.getName().equalsIgnoreCase(mCurrentSubscriber.get().getName())) {
                    mCurrentSubscriber.clear();
                    mCurrentSubscriber = null;
                }
            }

            if (mSubscribers.containsKey(subscriber.getName())) {
                mSubscribers.remove(subscriber.getName());
            }

            checkNullSubscriber();
        }
    }

    /**
     * Установить текущего подписчика
     *
     * @param subscriber подписчик
     */
    @Override
    public synchronized void setCurrentSubscriber(final ILifecycleSubscriber subscriber) {
        if (subscriber != null) {
            mCurrentSubscriber = new WeakReference<>(subscriber);
        }
    }

    private synchronized void checkNullSubscriber() {
        for (Map.Entry<String, WeakReference<ILifecycleSubscriber>> entry : mSubscribers.entrySet()) {
            if (entry.getValue().get() == null) {
                mSubscribers.remove(entry.getKey());
            }
        }
    }

    /**
     * Получить AbstractActivity
     *
     * @return the AbstractActivity
     */
    @Override
    public synchronized AbstractActivity getActivity() {
        final AbstractActivity activity = getCurrentActivity();
        if (activity != null) {
            return activity;
        }

        for (WeakReference<ILifecycleSubscriber> weakReference : mSubscribers.values()) {
            final ILifecycleSubscriber subscriber = weakReference.get();
            if (subscriber != null && subscriber instanceof AbstractActivity) {
                return (AbstractActivity) subscriber;
            }
        }
        return null;
    }

    /**
     * Получить текущую AbstractActivity.
     *
     * @return текущая AbstractActivity
     */
    @Override
    public synchronized AbstractActivity getCurrentActivity() {
        if (mCurrentSubscriber != null && mCurrentSubscriber.get() != null) {
            final ILifecycleSubscriber subscriber = mCurrentSubscriber.get();
            if (subscriber != null && subscriber instanceof AbstractActivity) {
                return (AbstractActivity) subscriber;
            }
        }
        return null;
    }


    private synchronized void startActivity(final StartActivityEvent event) {
        if (event != null) {
            final Context context = ApplicationController.getInstance();
            if (context != null) {
                context.startActivity(event.getIntent());
            }
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public ActivityPresenter getActivityPresenter() {
        final AbstractActivity activity = getCurrentActivity();
        if (activity != null) {
            return activity.getActivityPresenter();
        }
        return null;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onStartActivityEvent(final StartActivityEvent event) {
        startActivity(event);
    }


}
