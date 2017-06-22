package com.cleanarchitecture.shishkin.api.mail;

import android.widget.Toast;

import com.cleanarchitecture.shishkin.api.controller.ActivityController;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.api.event.ui.ShowToastEvent;

public class ShowToastMail extends AbstractMail {

    private static final String NAME = ShowToastMail.class.getName();

    private String mMessage;
    private int mDuration = Toast.LENGTH_SHORT;
    private int mType = ActivityController.TOAST_TYPE_INFO;

    public ShowToastMail(final String address, final String message) {
        super(address);

        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }

    public int getDuration() {
        return mDuration;
    }

    public ShowToastMail setDuration(int duration) {
        this.mDuration = duration;
        return this;
    }

    public int getType() {
        return mType;
    }

    public ShowToastMail setType(int type) {
        this.mType = type;
        return this;
    }

    @Override
    public void read(final IMailSubscriber subscriber) {
        AdminUtils.postEvent(new ShowToastEvent(mMessage)
                .setDuration(mDuration)
                .setType(mType));
    }

    @Override
    public IMail copy() {
        return new ShowToastMail(getAddress(), getMessage())
                .setDuration(getDuration())
                .setType(getType());
    }

    @Override
    public String getName() {
        return NAME;
    }

}
