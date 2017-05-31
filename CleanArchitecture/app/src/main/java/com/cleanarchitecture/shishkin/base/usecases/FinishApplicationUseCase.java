package com.cleanarchitecture.shishkin.base.usecases;

import com.cleanarchitecture.shishkin.base.controller.Controllers;
import com.cleanarchitecture.shishkin.base.controller.EventBusController;
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
        final IActivity subscriber = Controllers.getInstance().getLifecycleController().getActivity();
        if (subscriber != null && subscriber.getActivityPresenter() != null) {
            subscriber.getActivityPresenter().hideKeyboard();
        }

        // finish all activities и LiveLongBackgroundIntentService
        EventBusController.getInstance().post(new FinishApplicationEvent());

        // очистить кэш в памяти
        MemoryCache.getInstance().clearAll();
    }

    @Override
    public String getName() {
        return NAME;
    }
}
