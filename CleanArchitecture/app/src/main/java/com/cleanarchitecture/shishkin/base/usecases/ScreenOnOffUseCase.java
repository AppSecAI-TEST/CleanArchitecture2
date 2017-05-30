package com.cleanarchitecture.shishkin.base.usecases;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.event.OnScreenOffEvent;

/**
 * Команда - блокировка/разблокировка экрана
 */
public class ScreenOnOffUseCase extends AbstractUseCase{

    public static final String NAME = "ScreenOnOffUseCase";

    public static synchronized void onScreenOff() {
        // остановить все LiveLongBackgroundIntentService
        ApplicationController.getInstance().getEventController().post(new OnScreenOffEvent());
    }

    public static synchronized void onScreenOn() {
    }

    @Override
    public String getName() {
        return NAME;
    }
}
