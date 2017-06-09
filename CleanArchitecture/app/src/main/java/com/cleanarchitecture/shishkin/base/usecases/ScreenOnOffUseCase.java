package com.cleanarchitecture.shishkin.base.usecases;

import com.cleanarchitecture.shishkin.base.event.OnScreenOffEvent;
import com.cleanarchitecture.shishkin.base.utils.AdminUtils;

/**
 * Команда - блокировка/разблокировка экрана
 */
public class ScreenOnOffUseCase extends AbstractUseCase {

    public static final String NAME = "ScreenOnOffUseCase";

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
