package com.cleanarchitecture.shishkin.api.usecases;

import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.event.OnScreenOffEvent;

/**
 * Команда - блокировка/разблокировка экрана
 */
public class ScreenOnOffUseCase extends AbstractUseCase {

    public static final String NAME = ScreenOnOffUseCase.class.getName();

    public static synchronized void onScreenOff() {
        // остановить все LiveLongBackgroundIntentService
        AdminUtils.postEvent(new OnScreenOffEvent());
    }

    public static synchronized void onScreenOn() {
    }

    @Override
    public String getName() {
        return NAME;
    }
}
