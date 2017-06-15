package com.cleanarchitecture.shishkin.api.usecases;

import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.event.OnScreenOffEvent;
import com.cleanarchitecture.shishkin.api.event.OnScreenOnEvent;

/**
 * Команда - блокировка/разблокировка экрана
 */
public class ScreenOnOffUseCase extends AbstractUseCase {

    public static final String NAME = ScreenOnOffUseCase.class.getName();

    public static synchronized void onScreenOff() {
        // остановить все LiveLongBackgroundIntentService и LocationController
        AdminUtils.postEvent(new OnScreenOffEvent());
    }

    public static synchronized void onScreenOn() {
        // включить LocationController
        AdminUtils.postEvent(new OnScreenOnEvent());
    }

    @Override
    public String getName() {
        return NAME;
    }
}
