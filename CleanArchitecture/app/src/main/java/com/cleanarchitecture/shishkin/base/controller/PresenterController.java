package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.presenter.IPresenter;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Контроллер презенторов приложения
 */
@SuppressWarnings("unused")
public class PresenterController extends AbstractController<IPresenter>
        implements IPresenterController {

    public static final String NAME = "PresenterController";

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
    public synchronized IPresenter getPresenter(final String name) {
        if (getSubscribers().containsKey(name)) {
            for (Map.Entry<String, WeakReference<IPresenter>> entry : getSubscribers().entrySet()) {
                if (entry.getValue().get().getName().equalsIgnoreCase(name)) {
                    return entry.getValue().get();
                }
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return "IPresenter";
    }
}
