package com.cleanarchitecture.shishkin.api.controller;

import com.cleanarchitecture.shishkin.api.presenter.IPresenter;

/**
 * Контроллер презенторов приложения
 */
@SuppressWarnings("unused")
public class PresenterController extends AbstractController<IPresenter>
        implements IPresenterController {

    public static final String NAME = PresenterController.class.getName();
    public static final String SUBSCRIBER_TYPE = IPresenter.class.getName();

    public PresenterController() {
        super();
    }

    @Override
    public synchronized void register(final IPresenter subscriber) {
        if (subscriber != null && subscriber.isRegister()) {
            super.register(subscriber);
        }
    }

    @Override
    public synchronized void unregister(final IPresenter subscriber) {
        if (subscriber != null && subscriber.isRegister()) {
            super.unregister(subscriber);
        }
    }

    @Override
    public synchronized IPresenter getPresenter(final String name) {
        return getSubscriber(name);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return SUBSCRIBER_TYPE;
    }
}
