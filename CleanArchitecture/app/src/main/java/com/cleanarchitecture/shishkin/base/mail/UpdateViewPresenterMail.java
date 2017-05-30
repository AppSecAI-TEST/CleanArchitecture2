package com.cleanarchitecture.shishkin.base.mail;

import com.cleanarchitecture.shishkin.base.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.base.presenter.IPresenter;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;

public class UpdateViewPresenterMail extends AbstractMail {

    private static final String NAME = "UpdateViewPresenterMail";

    public UpdateViewPresenterMail(final String address) {
        super(address);
    }

    @Override
    public void read(final IMailSubscriber subscriber) {
        if (subscriber != null && subscriber instanceof IPresenter) {
            final IPresenter presenter = (IPresenter) subscriber;
            presenter.updateView();
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isCheckDublicate() {
        return true;
    }

}
