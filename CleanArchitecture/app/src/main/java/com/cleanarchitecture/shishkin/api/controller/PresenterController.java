package com.cleanarchitecture.shishkin.api.controller;

import android.os.Bundle;

import com.cleanarchitecture.shishkin.api.presenter.IPresenter;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Контроллер презенторов приложения
 */
@SuppressWarnings("unused")
public class PresenterController extends AbstractController<IPresenter>
        implements IPresenterController {

    public static final String NAME = PresenterController.class.getName();
    public static final String SUBSCRIBER_TYPE = IPresenter.class.getName();

    private Map<String, Bundle> mStates = Collections.synchronizedMap(new ConcurrentHashMap<String, Bundle>());

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

    @Override
    public synchronized void saveState(final String name, final Bundle state) {
        if (!StringUtils.isNullOrEmpty(name) && state != null) {
            mStates.put(name, state);
        }
    }

    @Override
    public synchronized Bundle restoreState(final String name) {
        if (!StringUtils.isNullOrEmpty(name)) {
            return mStates.get(name);
        }
        return null;
    }

    @Override
    public synchronized void clearState(final String name) {
        if (!StringUtils.isNullOrEmpty(name)) {
            mStates.remove(name);
        }
    }

}
