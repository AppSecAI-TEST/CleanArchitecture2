package com.cleanarchitecture.shishkin.api.presenter;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.event.ui.ShowMessageEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseFinishApplicationEvent;

import java.util.Timer;
import java.util.TimerTask;

public class OnBackPressedPresenter extends AbstractPresenter<Void> {
    private static final String NAME = OnBackPressedPresenter.class.getName();

    private boolean mDoubleBackPressedOnce = false;
    private Timer mTimer;

    public boolean onClick() {
        if (validate()) {
            if (!mDoubleBackPressedOnce) {
                final Context context = AdminUtils.getContext();
                if (context != null) {
                    mDoubleBackPressedOnce = true;
                    AdminUtils.postEvent(new ShowMessageEvent(context.getString(R.string.double_back_pressed)).setAction(context.getString(R.string.exit)));
                    startTimer();
                }
            } else {
                AdminUtils.postEvent(new UseCaseFinishApplicationEvent());
                return true;
            }
        }
        return false;
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
    public void onDestroyState() {
        super.onDestroyState();

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
