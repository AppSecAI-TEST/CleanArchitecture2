package com.cleanarchitecture.shishkin.api.controller;

import com.cleanarchitecture.shishkin.api.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.api.event.OnUserIteractionEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseFinishApplicationEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class UserIteractionController implements IModule, IModuleSubscriber {
    public static final String NAME = UserIteractionController.class.getName();
    private static final long TIMEOUT = TimeUnit.MINUTES.toMillis(10);

    private Timer mTimer = null;
    private boolean isStoped = false;

    public UserIteractionController() {
        startTimer();
    }

    private synchronized void startTimer() {
        stopTimer();

        if (!isStoped) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    stopTimer();
                    isStoped = true;

                    AdminUtils.postEvent(new UseCaseFinishApplicationEvent());
                }
            }, TIMEOUT);
        }
    }

    private synchronized void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return null;
    }

    @Override
    public List<String> hasSubscriberType() {
        final ArrayList<String> list = new ArrayList<>();
        list.add(EventBusController.SUBSCRIBER_TYPE);
        return list;
    }

    public void setStoped(boolean stoped) {
        isStoped = stoped;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onUserIteractionEvent(final OnUserIteractionEvent event) {
        startTimer();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public synchronized void onFinishApplicationEvent(FinishApplicationEvent event) {
        stopTimer();
        isStoped = true;
    }

}
