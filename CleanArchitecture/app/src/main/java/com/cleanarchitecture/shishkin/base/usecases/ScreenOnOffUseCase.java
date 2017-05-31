package com.cleanarchitecture.shishkin.base.usecases;

import com.cleanarchitecture.shishkin.base.controller.Controllers;
import com.cleanarchitecture.shishkin.base.controller.EventBusController;
import com.cleanarchitecture.shishkin.base.event.OnScreenOffEvent;

/**
 * Команда - блокировка/разблокировка экрана
 */
public class ScreenOnOffUseCase extends AbstractUseCase{

    public static final String NAME = "ScreenOnOffUseCase";

    public static synchronized void onScreenOff() {
        // остановить все LiveLongBackgroundIntentService
        EventBusController.getInstance().post(new OnScreenOffEvent());
    }

    public static synchronized void onScreenOn() {
    }

    @Override
    public String getName() {
        return NAME;
    }
}
