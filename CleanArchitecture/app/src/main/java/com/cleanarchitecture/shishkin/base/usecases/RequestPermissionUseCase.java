package com.cleanarchitecture.shishkin.base.usecases;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.controller.ActivityController;
import com.cleanarchitecture.shishkin.base.event.OnPermisionDeniedEvent;
import com.cleanarchitecture.shishkin.base.event.OnPermisionGrantedEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseRequestPermissionEvent;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;
import com.github.snowdream.android.util.Log;

/**
 * Команда - запрос прав приложением
 */
public class RequestPermissionUseCase extends AbstractUseCase {
    public static final String NAME = "RequestPermissionUseCase";

    public static synchronized void request(final UseCaseRequestPermissionEvent event) {
        final String permission = event.getPermission();

        final Context context = ApplicationController.getInstance();
        if (context == null) {
            return;
        }

        final int status = ApplicationUtils.getStatusPermission(permission);
        switch (permission) {
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                switch (status) {
                    case PackageManager.PERMISSION_GRANTED:
                        enableLog();
                        break;

                    case PackageManager.PERMISSION_DENIED:
                        disabledLog();
                        if (!ApplicationController.getInstance().getUseCasesController().isSystemDialogShown()) {
                            ApplicationController.getInstance().getActivityController().grantPermission(permission, ApplicationController.getInstance().getString(R.string.permission_write_external_storage));
                        }
                        break;

                }
                break;

            case Manifest.permission.READ_CONTACTS:
                switch (status) {
                    case PackageManager.PERMISSION_GRANTED:
                        break;

                    case PackageManager.PERMISSION_DENIED:
                        if (!ApplicationController.getInstance().getUseCasesController().isSystemDialogShown()) {
                            ApplicationController.getInstance().getActivityController().grantPermission(permission, ApplicationController.getInstance().getString(R.string.permission_read_contacts));
                        }
                        break;

                }
                break;

            case Manifest.permission.CALL_PHONE:
                switch (status) {
                    case PackageManager.PERMISSION_GRANTED:
                        break;

                    case PackageManager.PERMISSION_DENIED:
                        if (!ApplicationController.getInstance().getUseCasesController().isSystemDialogShown()) {
                            ApplicationController.getInstance().getActivityController().grantPermission(permission, ApplicationController.getInstance().getString(R.string.permission_call_phone));
                        }
                        break;

                }
                break;

        }
    }

    private static void enableLog() {
        Log.setEnabled(true);
        Log.setLog2FileEnabled(true);
    }

    private static void disabledLog() {
        Log.setEnabled(false);
        Log.setLog2FileEnabled(false);
    }

    public static synchronized void grantedPermision(final OnPermisionGrantedEvent event) {
        final String permission = event.getPermission();
        switch (permission) {
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                enableLog();
                break;

        }

    }

    public static synchronized void deniedPermision(final OnPermisionDeniedEvent event) {
        final String permission = event.getPermission();
        switch (permission) {
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                disabledLog();
                break;

        }

    }

    @Override
    public String getName() {
        return NAME;
    }
}
