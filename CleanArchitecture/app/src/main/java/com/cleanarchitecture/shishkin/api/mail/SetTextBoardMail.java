package com.cleanarchitecture.shishkin.api.mail;

import com.cleanarchitecture.shishkin.api.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.api.presenter.ExpandableBoardPresenter;
import com.cleanarchitecture.shishkin.api.presenter.IPresenter;

public class SetTextBoardMail extends AbstractMail {

    private static final String NAME = SetTextBoardMail.class.getName();

    private String mMessage;

    public SetTextBoardMail(final String message) {
        super(ExpandableBoardPresenter.NAME);

        mMessage = message;
    }

    @Override
    public void read(final IMailSubscriber subscriber) {
        if (subscriber != null && subscriber instanceof IPresenter) {
            ((ExpandableBoardPresenter) subscriber).setText(mMessage);
        }
    }

    @Override
    public IMail copy() {
        return new SetTextBoardMail(mMessage);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isCheckDublicate() {
        return false;
    }

}
