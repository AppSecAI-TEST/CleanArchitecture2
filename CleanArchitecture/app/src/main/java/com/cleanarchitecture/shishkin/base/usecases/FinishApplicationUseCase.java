package com.cleanarchitecture.shishkin.base.usecases;

import com.cleanarchitecture.shishkin.base.controller.Admin;
import com.cleanarchitecture.shishkin.base.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.base.event.ui.HideKeyboardEvent;
import com.cleanarchitecture.shishkin.base.storage.IStorage;
import com.cleanarchitecture.shishkin.base.storage.MemoryCache;
import com.cleanarchitecture.shishkin.base.utils.AdminUtils;

/**
 * Команда - выход из приложения
 */
public class FinishApplicationUseCase extends AbstractUseCase {
    public static final String NAME = "FinishApplicationUseCase";

    public static synchronized void onFinishApplication() {

        AdminUtils.postEvent(new HideKeyboardEvent());

        // finish all activities и LiveLongBackgroundIntentService
        AdminUtils.postEvent(new FinishApplicationEvent());

        // очистить кэш в памяти
        final IStorage memoryCache = Admin.getInstance().get(MemoryCache.NAME);
        if (memoryCache != null) {
            memoryCache.clearAll();
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
