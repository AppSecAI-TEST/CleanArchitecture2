package com.cleanarchitecture.shishkin.base.mail;

import android.widget.Toast;

import com.cleanarchitecture.shishkin.base.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.base.event.ui.ShowToastEvent;
import com.cleanarchitecture.shishkin.base.presenter.ActivityPresenter;

public class ShowToastMail extends AbstractMail {

    private static final String NAME = "ShowToastMail";

    private String mMessage;
    private int mDuration = Toast.LENGTH_SHORT;
    private int mType = ActivityPresenter.TOAST_TYPE_INFO;

    public ShowToastMail(final String address, final String message) {
        super(address);

        mMessage = message;
    }

    public ShowToastMail(final String address, final String message, final int duration) {
        super(address);

        mMessage = message;
        mDuration = duration;
    }

    public ShowToastMail(final String address, final String message, final int duration, final int type) {
        super(address);

        mMessage = message;
        mDuration = duration;
        mType = type;
    }

    @Override
    public void read(final IMailSubscriber subscriber) {
        postEvent(new ShowToastEvent(mMessage, mDuration));
    }

    @Override
    public IMail copy() {
        return new ShowToastMail(getAddress(), mMessage, mDuration, mType);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
