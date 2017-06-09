package com.cleanarchitecture.shishkin.base.mail;

import android.widget.Toast;

import com.cleanarchitecture.shishkin.base.controller.ActivityController;
import com.cleanarchitecture.shishkin.base.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.base.event.ui.ShowToastEvent;
import com.cleanarchitecture.shishkin.base.utils.AdminUtils;

public class ShowToastMail extends AbstractMail {

    private static final String NAME = "ShowToastMail";

    private String mMessage;
    private int mDuration = Toast.LENGTH_LONG;
    private int mType = ActivityController.TOAST_TYPE_INFO;

    public ShowToastMail(final String address, final String message) {
        super(address);

        mMessage = message;
    }

    public ShowToastMail setDuration(int duration) {
        this.mDuration = duration;
        return this;
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
    public String getName() {
        return NAME;
    }

}
