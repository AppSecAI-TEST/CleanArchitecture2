package com.cleanarchitecture.shishkin.api.mail;

import com.cleanarchitecture.shishkin.api.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.api.presenter.IPresenter;

public class UpdateViewPresenterMail extends AbstractMail {

    private static final String NAME = UpdateViewPresenterMail.class.getName();

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
    public IMail copy() {
        return new UpdateViewPresenterMail(getAddress());
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
