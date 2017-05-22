package com.cleanarchitecture.shishkin.base.usecases;

import com.cleanarchitecture.shishkin.base.controller.AbstractController;
import com.cleanarchitecture.shishkin.base.controller.EventController;
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
public class UseCasesController extends AbstractController {
    private UseCasesController() {
        mLock = new ReentrantLock();

        EventController.getInstance().register(this);
    }

    private static final String NAME = "UseCasesController";

    private static volatile UseCasesController sInstance;

    private boolean mSystemDialogShown = false;
    private ReentrantLock mLock;

    public static void instantiate() {
        if (sInstance == null) {
            synchronized (UseCasesController.class) {
                if (sInstance == null) {
                    sInstance = new UseCasesController();
                }
            }
        }
    }

    public static UseCasesController getInstance() {
        instantiate();
        return sInstance;
    }

    public synchronized boolean isSystemDialogShown() {
        return mSystemDialogShown;
    }

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
