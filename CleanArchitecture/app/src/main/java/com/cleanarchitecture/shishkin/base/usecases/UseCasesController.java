package com.cleanarchitecture.shishkin.base.usecases;

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
import com.github.snowdream.android.util.Log;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Контроллер бизнес и пользовательской логики в приложении
 */
@SuppressWarnings("unused")
public class UseCasesController extends AbstractController implements IUseCasesController {
    public static final String NAME = "UseCasesController";
    private boolean mSystemDialogShown = false;
    private ReentrantLock mLock;

    public UseCasesController() {
        mLock = new ReentrantLock();

        EventBusController.getInstance().register(this);
    }

    @Override
    public synchronized boolean isSystemDialogShown() {
        return mSystemDialogShown;
    }

    @Override
    public synchronized void setSystemDialogShown(boolean shown) {
        mLock.lock();
        try {
            mSystemDialogShown = shown;
        } catch (Exception e) {
            Log.e(NAME, e.getMessage());
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public String getName() {
        return NAME;
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
        if (!isSystemDialogShown()) {
            RequestPermissionUseCase.request(event);
        }
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
