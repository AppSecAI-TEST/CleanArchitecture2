package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.event.OnUserIteractionEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseFinishApplicationEvent;
import com.cleanarchitecture.shishkin.base.utils.AdminUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UserIteractionController implements IModule, IModuleSubscriber, AutoCompleteHandler.OnHandleEventListener<OnUserIteractionEvent>, AutoCompleteHandler.OnShutdownListener {
    public static final String NAME = "UserIteractionController";
    private static final long TIMEOUT = TimeUnit.MINUTES.toMillis(10);

    private AutoCompleteHandler<OnUserIteractionEvent> mServiceHandler = null;

    public UserIteractionController() {
        mServiceHandler = new AutoCompleteHandler<OnUserIteractionEvent>("LiveLongAndProsperIntentService [" + NAME + "]");
        mServiceHandler.setOnHandleEventListener(this);
        mServiceHandler.setOnShutdownListener(this);
        mServiceHandler.setShutdownTimeout(TIMEOUT);
        mServiceHandler.post(new OnUserIteractionEvent());
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

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onUserIteractionEvent(final OnUserIteractionEvent event) {
        mServiceHandler.post(event);
    }

    @Override
    public void onHandleEvent(OnUserIteractionEvent event) {
    }

    @Override
    public void onShutdown(AutoCompleteHandler handler) {
        AdminUtils.postEvent(new UseCaseFinishApplicationEvent());
    }
}
