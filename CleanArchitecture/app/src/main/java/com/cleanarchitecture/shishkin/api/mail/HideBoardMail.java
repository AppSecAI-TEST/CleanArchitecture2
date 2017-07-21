package com.cleanarchitecture.shishkin.api.mail;

import com.cleanarchitecture.shishkin.api.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.api.presenter.ExpandableBoardPresenter;
import com.cleanarchitecture.shishkin.api.presenter.IPresenter;

public class HideBoardMail extends AbstractMail {

    private static final String NAME = HideBoardMail.class.getName();

    public HideBoardMail() {
        super(ExpandableBoardPresenter.NAME);
    }

    @Override
    public void read(final IMailSubscriber subscriber) {
        if (subscriber != null && subscriber instanceof IPresenter) {
            ((ExpandableBoardPresenter) subscriber).hideBoard();
        }
    }

    @Override
    public IMail copy() {
        return new HideBoardMail();
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
