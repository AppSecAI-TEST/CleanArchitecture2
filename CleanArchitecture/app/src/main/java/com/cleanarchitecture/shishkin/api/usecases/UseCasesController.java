package com.cleanarchitecture.shishkin.api.usecases;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AbstractController;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.ApplicationController;
import com.cleanarchitecture.shishkin.api.controller.EventBusController;
import com.cleanarchitecture.shishkin.api.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.api.event.OnPermisionDeniedEvent;
import com.cleanarchitecture.shishkin.api.event.OnPermisionGrantedEvent;
import com.cleanarchitecture.shishkin.api.event.ui.OnSnackBarClickEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseFinishApplicationEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseOnLowMemoryEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseOnScreenOffEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseOnScreenOnEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseRequestPermissionEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseStartApplicationEvent;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Контроллер бизнес и пользовательской логики в приложении
 */
@SuppressWarnings("unused")
public class UseCasesController extends AbstractController implements IUseCasesController, IModuleSubscriber {
    public static final String NAME = UseCasesController.class.getName();

    private BroadcastReceiver mScreenOnOffReceiver;

    private boolean mIsFinished = false;

    public UseCasesController() {
        registerScreenOnOffBroadcastReceiver();
    }

    private void registerScreenOnOffBroadcastReceiver() {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_SCREEN_ON);
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

            mScreenOnOffReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final String strAction = intent.getAction();

                    if (strAction.equals(Intent.ACTION_SCREEN_OFF)) {
                        AdminUtils.postEvent(new UseCaseOnScreenOffEvent());
                    } else {
                        AdminUtils.postEvent(new UseCaseOnScreenOnEvent());
                    }
                }
            };

            context.registerReceiver(mScreenOnOffReceiver, intentFilter);
        }
    }

    @Override
    public void onUnRegisterModule() {
        if (mScreenOnOffReceiver != null) {
            final Context context = AdminUtils.getContext();
            if (context != null) {
                context.unregisterReceiver(mScreenOnOffReceiver);
            }
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<String> getSubscription() {
        return StringUtils.arrayToList(EventBusController.NAME);
    }

    @Override
    public boolean isApplicationFinished() {
        return mIsFinished;
    }

    @Override
    public String getDescription() {
        final Context context = ApplicationController.getInstance();
        if (context != null) {
            return context.getString(R.string.module_use_cases);
        }
        return "Use cases controller";
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSnackBarClickEvent(final OnSnackBarClickEvent event) {
        SnackbarOnClickUseCase.onClick(event);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onUseCaseFinishApplicationEvent(final UseCaseFinishApplicationEvent event) {
        mIsFinished = true;
        FinishApplicationUseCase.onFinishApplication();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onUseCaseStartApplicationEvent(final UseCaseStartApplicationEvent event) {
        mIsFinished = false;
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
