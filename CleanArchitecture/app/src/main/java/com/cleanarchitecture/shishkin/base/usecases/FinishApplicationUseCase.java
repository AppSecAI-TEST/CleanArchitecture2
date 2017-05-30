package com.cleanarchitecture.shishkin.base.usecases;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.controller.LifecycleController;
import com.cleanarchitecture.shishkin.base.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.base.storage.MemoryCache;
import com.cleanarchitecture.shishkin.base.ui.activity.IActivity;

/**
 * Команда - выход из приложения
 */
public class FinishApplicationUseCase  extends AbstractUseCase{
    public static final String NAME = "FinishApplicationUseCase";

    public static synchronized void onFinishApplication() {
        // скрыть клавиатуру
        final IActivity subscriber = LifecycleController.getInstance().getActivity();
        if (subscriber != null && subscriber.getActivityPresenter() != null) {
            subscriber.getActivityPresenter().hideKeyboard();
        }

        // finish all activities и LiveLongBackgroundIntentService
        ApplicationController.getInstance().getEventController().post(new FinishApplicationEvent());

        // очистить кэш в памяти
        MemoryCache.getInstance().clearAll();
    }

    @Override
    public String getName() {
        return NAME;
    }
}
