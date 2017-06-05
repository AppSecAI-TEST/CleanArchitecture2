package com.cleanarchitecture.shishkin.base.usecases;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.controller.AbstractController;
import com.cleanarchitecture.shishkin.base.controller.EventBusController;
import com.cleanarchitecture.shishkin.base.event.OnPermisionDeniedEvent;
import com.cleanarchitecture.shishkin.base.event.OnPermisionGrantedEvent;
import com.cleanarchitecture.shishkin.base.event.ui.OnSnackBarClickEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseFinishApplicationEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseOnLowMemoryEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseOnScreenOffEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseOnScreenOnEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseRequestPermissionEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Контроллер бизнес и пользовательской логики в приложении
 */
@SuppressWarnings("unused")
public class UseCasesController extends AbstractController implements IUseCasesController {
    public static final String NAME = "UseCasesController";

    public UseCasesController() {
        EventBusController.getInstance().register(this);
    }

    private void registerScreenOnOffBroadcastReceiver() {
        final Context context = ApplicationController.getInstance();
        if (context != null) {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_SCREEN_ON);
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

            final BroadcastReceiver screenOnOffReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final String strAction = intent.getAction();

                    if (strAction.equals(Intent.ACTION_SCREEN_OFF)) {
                        EventBusController.getInstance().post(new UseCaseOnScreenOffEvent());
                    } else {
                        EventBusController.getInstance().post(new UseCaseOnScreenOnEvent());
                    }
                }
            };

            context.registerReceiver(screenOnOffReceiver, intentFilter);
        }

    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return null;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSnackBarClickEvent(final OnSnackBarClickEvent event) {
        SnackbarOnClickUseCase.onClick(event);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onFinishApplicationUseCaseEvent(final UseCaseFinishApplicationEvent event) {
        FinishApplicationUseCase.onFinishApplication();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRequestPermissionUseCaseEvent(final UseCaseRequestPermissionEvent event) {
        RequestPermissionUseCase.request(event);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRequestPermissionUseCaseEvent(final OnPermisionGrantedEvent event) {
        RequestPermissionUseCase.grantedPermision(event);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRequestPermissionUseCaseEvent(final OnPermisionDeniedEvent event) {
        RequestPermissionUseCase.deniedPermision(event);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onUseCaseOnScreenOffEvent(final UseCaseOnScreenOffEvent event) {
        ScreenOnOffUseCase.onScreenOff();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onUseCaseOnScreenOnEvent(final UseCaseOnScreenOnEvent event) {
        ScreenOnOffUseCase.onScreenOn();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onUseCaseOnLowMemoryEvent(final UseCaseOnLowMemoryEvent event) {
        LowMemoryUseCase.onLowMemory();
    }

}
