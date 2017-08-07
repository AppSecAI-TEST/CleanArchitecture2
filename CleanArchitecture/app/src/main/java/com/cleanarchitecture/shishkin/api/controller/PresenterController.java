package com.cleanarchitecture.shishkin.api.controller;

import android.content.Context;
import android.os.Bundle;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.presenter.IPresenter;
import com.cleanarchitecture.shishkin.api.usecases.IUseCasesController;
import com.cleanarchitecture.shishkin.api.usecases.UseCasesController;
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
    public synchronized void saveStateData(final String name, final Bundle state) {
        if (!StringUtils.isNullOrEmpty(name) && state != null) {
            final IUseCasesController controller = Admin.getInstance().get(UseCasesController.NAME);
            if (controller != null && !controller.isApplicationFinished()) {
                mStates.put(name, state);
            }
        }
    }

    @Override
    public synchronized Bundle restoreStateData(final String name) {
        if (!StringUtils.isNullOrEmpty(name)) {
            return mStates.get(name);
        }
        return null;
    }

    @Override
    public synchronized void clearStateData(final String name) {
        if (!StringUtils.isNullOrEmpty(name)) {
            mStates.remove(name);
        }
    }

    @Override
    public String getDescription() {
        final Context context = ApplicationController.getInstance();
        if (context != null) {
            return context.getString(R.string.module_presenter);
        }
        return "Presenter controller";
    }

    @Override
    public boolean isPersistent() {
        return true;
    }


}
