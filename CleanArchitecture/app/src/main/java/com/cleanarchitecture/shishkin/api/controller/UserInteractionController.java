package com.cleanarchitecture.shishkin.api.controller;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.api.event.OnUserIteractionEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseFinishApplicationEvent;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class UserInteractionController extends AbstractModule implements IModuleSubscriber {
    public static final String NAME = UserInteractionController.class.getName();
    private static final long TIMEOUT = TimeUnit.MINUTES.toMillis(10);

    private Timer mTimer = null;
    private boolean isStoped = false;

    public UserInteractionController() {
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
        return StringUtils.arrayToList(EventBusController.SUBSCRIBER_TYPE);
    }

    public synchronized void setStoped(boolean stoped) {
        isStoped = stoped;
    }


    @Override
    public String getDescription() {
        final Context context = ApplicationController.getInstance();
        if (context != null) {
            return context.getString(R.string.module_user_interaction);
        }
        return "User interaction controller";
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
