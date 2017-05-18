package com.cleanarchitecture.shishkin.base.usecases;

import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.controller.LifecycleController;
import com.cleanarchitecture.shishkin.base.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.base.ui.activity.IActivity;

/**
 * Команда - выход из приложения
 */
public class FinishApplicationUseCase  extends AbstractUseCase{
    public static final String NAME = "FinishApplicationUseCase";

    public static synchronized void onFinishApplication() {
        final IActivity subscriber = LifecycleController.getInstance().getActivity();
        if (subscriber != null && subscriber.getActivityPresenter() != null) {
            subscriber.getActivityPresenter().hideKeyboard();
        }

        EventController.getInstance().post(new FinishApplicationEvent());
    }

    @Override
    public String getName() {
        return NAME;
    }
}
