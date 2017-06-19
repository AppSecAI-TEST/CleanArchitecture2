package com.cleanarchitecture.shishkin.api.mail;

import android.support.design.widget.Snackbar;

import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.api.event.ui.ShowMessageEvent;

public class ShowMessageMail extends AbstractMail {

    private static final String NAME = ShowMessageMail.class.getName();

    private String mMessage;
    private String mAction;
    private int mDuration = Snackbar.LENGTH_LONG;

    public ShowMessageMail(final String address, final String message) {
        super(address);

        mMessage = message;
    }

    public ShowMessageMail setAction(String action) {
        this.mAction = action;
        return this;
    }

    public ShowMessageMail setDuration(int duration) {
        this.mDuration = duration;
        return this;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getAction() {
        return mAction;
    }

    public int getDuration() {
        return mDuration;
    }

    @Override
    public void read(final IMailSubscriber subscriber) {
        AdminUtils.postEvent(new ShowMessageEvent(mMessage)
                .setDuration(mDuration)
                .setAction(mAction));
    }

    @Override
    public IMail copy() {
        return new ShowMessageMail(getAddress(), getMessage())
                .setDuration(getDuration())
                .setAction(getAction());
    }

    @Override
    public String getName() {
        return NAME;
    }

}
