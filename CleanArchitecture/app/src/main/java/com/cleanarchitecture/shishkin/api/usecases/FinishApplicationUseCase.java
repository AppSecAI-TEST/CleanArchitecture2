package com.cleanarchitecture.shishkin.api.usecases;

import com.cleanarchitecture.shishkin.api.controller.Admin;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.api.event.ui.HideKeyboardEvent;
import com.cleanarchitecture.shishkin.api.storage.IStorage;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Команда - выход из приложения
 */
public class FinishApplicationUseCase extends AbstractUseCase {
    public static final String NAME = FinishApplicationUseCase.class.getName();

    public static synchronized void onFinishApplication() {

        AdminUtils.postEvent(new HideKeyboardEvent());

        // finish all activities и LiveLongBackgroundIntentService
        AdminUtils.postEvent(new FinishApplicationEvent());

        // очистить кэш в памяти
        final IStorage memoryCache = AdminUtils.getMemoryCache();
        if (memoryCache != null) {
            memoryCache.clearAll();
        }

        // очистить админа через 30 сек
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (Admin.getInstance().isFinishApplication()) {
                    Admin.getInstance().unregister();
                }
            }
        }, TimeUnit.SECONDS.toMillis(30));
    }

    @Override
    public String getName() {
        return NAME;
    }
}
