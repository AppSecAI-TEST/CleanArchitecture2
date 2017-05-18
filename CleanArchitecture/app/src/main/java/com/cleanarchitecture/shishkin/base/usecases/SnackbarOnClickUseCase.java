package com.cleanarchitecture.shishkin.base.usecases;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.event.ui.OnSnackBarClickEvent;

/**
 * Команда - нажатие на кнопку в панели Snackbar
 */
public class SnackbarOnClickUseCase extends AbstractUseCase{

    public static final String NAME = "SnackbarOnClickUseCase";

    public static synchronized void onClick(final OnSnackBarClickEvent event) {

        final String action = event.getText();
        final Context context = ApplicationController.getInstance();

        if (context != null) {
            if (action.equals(context.getString(R.string.exit))) {
                FinishApplicationUseCase.onFinishApplication();
            }
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
