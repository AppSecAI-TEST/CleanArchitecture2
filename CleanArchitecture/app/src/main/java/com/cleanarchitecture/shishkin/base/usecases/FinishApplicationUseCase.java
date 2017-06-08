package com.cleanarchitecture.shishkin.base.usecases;

import com.cleanarchitecture.shishkin.base.controller.Admin;
import com.cleanarchitecture.shishkin.base.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.base.event.ui.HideKeyboardEvent;
import com.cleanarchitecture.shishkin.base.storage.MemoryCache;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;

/**
 * Команда - выход из приложения
 */
public class FinishApplicationUseCase extends AbstractUseCase {
    public static final String NAME = "FinishApplicationUseCase";

    public static synchronized void onFinishApplication() {

        ApplicationUtils.postEvent(new HideKeyboardEvent());

        // finish all activities и LiveLongBackgroundIntentService
        ApplicationUtils.postEvent(new FinishApplicationEvent());

        // очистить кэш в памяти
        MemoryCache.getInstance().clearAll();
    }

    @Override
    public String getName() {
        return NAME;
    }
}
