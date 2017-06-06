package com.cleanarchitecture.shishkin.base.presenter;

import com.cleanarchitecture.shishkin.base.controller.Admin;
import com.cleanarchitecture.shishkin.base.controller.EventBusController;
import com.cleanarchitecture.shishkin.base.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.base.controller.MailController;
import com.cleanarchitecture.shishkin.base.controller.PresenterController;
import com.cleanarchitecture.shishkin.base.event.IEvent;
import com.cleanarchitecture.shishkin.base.lifecycle.Lifecycle;
import com.cleanarchitecture.shishkin.base.mail.UpdateViewPresenterMail;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPresenter<M> implements IPresenter<M>, IMailSubscriber {

    private M mModel = null;
    private Lifecycle mLifecycle = new Lifecycle(this);

    @Override
    public synchronized int getState() {
        return mLifecycle.getState();
    }

    @Override
    public synchronized void setState(final int state) {
        mLifecycle.setState(state);
    }

    @Override
    public void onCreateLifecycle() {
    }

    @Override
    public void onReadyLifecycle() {
        Admin.getInstance().register(this);
        updateView();
    }

    @Override
    public void onResumeLifecycle() {
        ApplicationUtils.readMail(this);
    }

    @Override
    public void onPauseLifecycle() {
    }

    @Override
    public void onDestroyLifecycle() {
        Admin.getInstance().unregister(this);
    }

    @Override
    public synchronized void setModel(final M model) {
        mModel = model;

        if (validate()) {
            if (getState() == Lifecycle.STATE_RESUME || getState() == Lifecycle.STATE_READY) {
                updateView();
            } else {
                ApplicationUtils.addMail(new UpdateViewPresenterMail(getName()));
            }
        }
    }

    @Override
    public synchronized M getModel() {
        return mModel;
    }

    @Override
    public synchronized void updateView() {
    }

    @Override
    public synchronized boolean validate() {
        return (mLifecycle.getState() != Lifecycle.STATE_DESTROY && mLifecycle.getState() != Lifecycle.STATE_CREATE);
    }

    @Override
    abstract public boolean isRegister();

    @Override
    abstract public String getName();

    @Override
    public List<String> hasSubscriberType() {
        final ArrayList<String> list = new ArrayList<>();
        list.add(PresenterController.SUBSCRIBER_TYPE);
        list.add(MailController.SUBSCRIBER_TYPE);
        return list;
    }

    @Override
    public void showProgressBar() {
    }

    @Override
    public void hideProgressBar() {
    }
}
