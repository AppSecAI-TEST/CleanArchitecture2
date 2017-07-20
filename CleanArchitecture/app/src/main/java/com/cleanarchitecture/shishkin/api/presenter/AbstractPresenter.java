package com.cleanarchitecture.shishkin.api.presenter;

import android.os.Bundle;

import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.api.controller.MailController;
import com.cleanarchitecture.shishkin.api.controller.PresenterController;
import com.cleanarchitecture.shishkin.common.state.ViewStateObserver;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPresenter<M> implements IPresenter<M>, IMailSubscriber {

    private M mModel = null;
    private ViewStateObserver mLifecycle = new ViewStateObserver(this);

    @Override
    public synchronized int getState() {
        return mLifecycle.getState();
    }

    @Override
    public synchronized void setState(final int state) {
        mLifecycle.setState(state);
    }

    @Override
    public void onCreateState() {
    }

    @Override
    public void onReadyState() {
        AdminUtils.register(this);
        updateView();
    }

    @Override
    public void onResumeState() {
        AdminUtils.readMail(this);
    }

    @Override
    public void onPauseState() {
    }

    @Override
    public void onDestroyState() {
        AdminUtils.unregister(this);
    }

    @Override
    public synchronized void setModel(final M model) {
        mModel = model;

        if (validate()) {
            updateView();

            //if (getState() == Lifecycle.STATE_RESUME || getState() == Lifecycle.STATE_READY) {
            //    updateView();
            //} else {
            //    AdminUtils.addMail(new UpdateViewPresenterMail(getName()));
            //}
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
        return (mLifecycle.getState() != ViewStateObserver.STATE_DESTROY && mLifecycle.getState() != ViewStateObserver.STATE_CREATE);
    }

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

    @Override
    public Bundle getStateData() {
        return null;
    }
}
