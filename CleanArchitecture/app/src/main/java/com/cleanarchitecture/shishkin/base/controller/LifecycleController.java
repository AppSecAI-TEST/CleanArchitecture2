package com.cleanarchitecture.shishkin.base.controller;

import android.content.Context;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.event.StartActivityEvent;
import com.cleanarchitecture.shishkin.base.ui.activity.AbstractActivity;
import com.cleanarchitecture.shishkin.base.ui.activity.AbstractContentActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

/**
 * Контроллер, отвечающий за жизненный цикл activities приложения
 */
@SuppressWarnings("unused")
public class LifecycleController extends AbstractController<ILifecycleSubscriber>
        implements ILifecycleController {

    public static final String NAME = "LifecycleController";

    public LifecycleController() {
        super();

        EventBusController.getInstance().register(this);
    }

    @Override
    public synchronized AbstractActivity getActivity() {
        final AbstractActivity activity = getCurrentActivity();
        if (activity != null) {
            return activity;
        }

        for (WeakReference<ILifecycleSubscriber> weakReference : getSubscribers().values()) {
            final ILifecycleSubscriber subscriber = weakReference.get();
            if (subscriber != null && subscriber instanceof AbstractActivity) {
                return (AbstractActivity) subscriber;
            }
        }
        return null;
    }

    @Override
    public synchronized AbstractActivity getCurrentActivity() {
        final ILifecycleSubscriber subscriber = getCurrentSubscriber();
        if (subscriber != null && subscriber instanceof AbstractActivity) {
            return (AbstractActivity) subscriber;
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
    public synchronized AbstractContentActivity getContentActivity() {
        final AbstractContentActivity activity = getCurrentContentActivity();
        if (activity != null) {
            return activity;
        }

        for (WeakReference<ILifecycleSubscriber> weakReference : getSubscribers().values()) {
            final ILifecycleSubscriber subscriber = weakReference.get();
            if (subscriber != null && subscriber instanceof AbstractContentActivity) {
                return (AbstractContentActivity) subscriber;
            }
        }
        return null;
    }

    @Override
    public synchronized AbstractContentActivity getCurrentContentActivity() {
        final ILifecycleSubscriber subscriber = getCurrentSubscriber();
        if (subscriber != null && subscriber instanceof AbstractContentActivity) {
            return (AbstractContentActivity) subscriber;
        }
        return null;
    }

    @Override
    public String getSubscriberType() {
        return "ILifecycleSubscriber";
    }

    @Override
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onStartActivityEvent(final StartActivityEvent event) {
        startActivity(event);
    }


}
