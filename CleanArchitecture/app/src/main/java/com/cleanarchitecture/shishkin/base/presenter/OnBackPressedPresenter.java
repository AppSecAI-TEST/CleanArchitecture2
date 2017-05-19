package com.cleanarchitecture.shishkin.base.presenter;

import android.content.Context;
import android.support.design.widget.Snackbar;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.event.ui.ShowMessageEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseFinishApplicationEvent;

import java.util.Timer;
import java.util.TimerTask;

public class OnBackPressedPresenter extends AbstractPresenter<Void> {
    private static final String NAME = "onBackPressedPresenter";

    private boolean mDoubleBackPressedOnce = false;
    private Timer mTimer;

    public void onClick() {
        if (validate()) {
            if (!mDoubleBackPressedOnce) {
                final Context context = ApplicationController.getInstance();
                if (context != null) {
                    mDoubleBackPressedOnce = true;
                    EventController.getInstance().post(new ShowMessageEvent(context.getString(R.string.double_back_pressed), Snackbar.LENGTH_LONG, context.getString(R.string.exit)));
                    startTimer();
                }
            } else {
                EventController.getInstance().post(new UseCaseFinishApplicationEvent());
            }
        }
    }

    private void startTimer() {
        if (mTimer != null) {
            stopTimer();
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mDoubleBackPressedOnce = false;
            }
        }, 3000);
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onDestroyLifecycle() {
        super.onDestroyLifecycle();

        stopTimer();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isRegister() {
        return false;
    }


}
