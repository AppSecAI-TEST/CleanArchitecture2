package com.cleanarchitecture.shishkin.base.presenter;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.controller.Controllers;
import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.controller.IEventVendor;
import com.cleanarchitecture.shishkin.base.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.base.controller.MailController;
import com.cleanarchitecture.shishkin.base.event.IEvent;
import com.cleanarchitecture.shishkin.base.lifecycle.Lifecycle;
import com.cleanarchitecture.shishkin.base.mail.IMail;
import com.cleanarchitecture.shishkin.base.mail.UpdateViewPresenterMail;

import java.util.List;

public abstract class AbstractPresenter<M> implements IPresenter<M>, IEventVendor, IMailSubscriber {

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
        Controllers.getInstance().getMailController().register(this);
        updateView();
    }

    @Override
    public void onResumeLifecycle() {
        readMail();
    }

    @Override
    public void onPauseLifecycle() {
    }

    @Override
    public void onDestroyLifecycle() {
        Controllers.getInstance().getMailController().unregister(this);
    }

    @Override
    public synchronized void setModel(final M model) {
        mModel = model;

        if (validate()) {
            if (getState() == Lifecycle.STATE_RESUME || getState() == Lifecycle.STATE_VIEW_CREATED) {
                updateView();
            } else {
                Controllers.getInstance().getMailController().addMail(new UpdateViewPresenterMail(getName()));
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

    abstract public boolean isRegister();

    @Override
    abstract public String getName();

    /**
     * послать событие на шину событий
     *
     * @param event событие
     */
    public void postEvent(IEvent event) {
        Controllers.getInstance().getEventController().post(event);
    }

    @Override
    public synchronized void readMail() {
        final List<IMail> list = Controllers.getInstance().getMailController().getMail(this);
        for (IMail mail : list) {
            mail.read(this);
            Controllers.getInstance().getMailController().removeMail(mail);
        }
    }

    @Override
    public void showProgressBar() {
    }

    @Override
    public void hideProgressBar() {
    }
}
