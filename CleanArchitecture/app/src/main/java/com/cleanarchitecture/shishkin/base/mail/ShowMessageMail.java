package com.cleanarchitecture.shishkin.base.mail;

import android.support.design.widget.Snackbar;

import com.cleanarchitecture.shishkin.base.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.base.event.ui.ShowMessageEvent;

public class ShowMessageMail extends AbstractMail{

    private static final String NAME = "ShowMessageMail";

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

    @Override
    public void read(final IMailSubscriber subscriber) {
        postEvent(new ShowMessageEvent(mMessage)
                .setDuration(mDuration)
                .setAction(mAction));
    }

    @Override
    public String getName() {
        return NAME;
    }

}
