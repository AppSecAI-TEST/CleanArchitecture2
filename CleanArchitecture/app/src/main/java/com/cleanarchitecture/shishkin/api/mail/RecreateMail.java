package com.cleanarchitecture.shishkin.api.mail;

import com.cleanarchitecture.shishkin.api.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.api.ui.activity.AbstractActivity;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;

public class RecreateMail extends AbstractMail {

    private static final String NAME = RecreateMail.class.getName();

    public RecreateMail(final String address) {
        super(address);
    }

    @Override
    public void read(final IMailSubscriber subscriber) {
        ApplicationUtils.runOnUiThread(() -> ((AbstractActivity) subscriber).recreate());
    }

    @Override
    public IMail copy() {
        return new RecreateMail(getAddress());
    }

    @Override
    public String getName() {
        return NAME;
    }

}
