package com.cleanarchitecture.shishkin.base.presenter;

import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.controller.IEventVendor;
import com.cleanarchitecture.shishkin.base.event.IEvent;
import com.cleanarchitecture.shishkin.base.lifecycle.Lifecycle;

public abstract class AbstractPresenter<M> implements IPresenter<M>, IEventVendor {

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
    public void onViewCreatedLifecycle() {
        updateView();
    }

    @Override
    public void onResumeLifecycle() {
    }

    @Override
    public void onPauseLifecycle() {
    }

    @Override
    public void onDestroyLifecycle() {
    }

    @Override
    public synchronized void setModel(final M model) {
        mModel = model;

        updateView();
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

    abstract public boolean isRegister();

    @Override
    abstract public String getName();

    /**
     * послать событие на шину событий
     *
     * @param event событие
     */
    public void postEvent(IEvent event) {
        EventController.getInstance().post(event);
    }

}
