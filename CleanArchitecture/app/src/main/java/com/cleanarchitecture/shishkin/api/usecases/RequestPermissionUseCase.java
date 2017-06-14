package com.cleanarchitecture.shishkin.api.usecases;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.ActivityController;
import com.cleanarchitecture.shishkin.api.controller.Admin;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.IActivityController;
import com.cleanarchitecture.shishkin.api.event.OnPermisionDeniedEvent;
import com.cleanarchitecture.shishkin.api.event.OnPermisionGrantedEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseRequestPermissionEvent;
import com.github.snowdream.android.util.Log;

/**
 * Команда - запрос прав приложением
 */
public class RequestPermissionUseCase extends AbstractUseCase {
    public static final String NAME = RequestPermissionUseCase.class.getName();

    public static synchronized void request(final UseCaseRequestPermissionEvent event) {
        final String permission = event.getPermission();

        final Context context = AdminUtils.getContext();
        if (context == null) {
            return;
        }

        final IActivityController controller = Admin.getInstance().get(ActivityController.NAME);
        if (controller != null) {
            final int status = AdminUtils.getStatusPermission(permission);
            switch (permission) {
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    switch (status) {
                        case PackageManager.PERMISSION_GRANTED:
                            enableLog();
                            break;

                        case PackageManager.PERMISSION_DENIED:
                            disabledLog();
                            controller.grantPermission(permission, context.getString(R.string.permission_write_external_storage));
                            break;

                    }
                    break;

                case Manifest.permission.READ_CONTACTS:
                    switch (status) {
                        case PackageManager.PERMISSION_GRANTED:
                            break;

                        case PackageManager.PERMISSION_DENIED:
                            controller.grantPermission(permission, context.getString(R.string.permission_read_contacts));
                            break;

                    }
                    break;

                case Manifest.permission.CALL_PHONE:
                    switch (status) {
                        case PackageManager.PERMISSION_GRANTED:
                            break;

                        case PackageManager.PERMISSION_DENIED:
                            controller.grantPermission(permission, context.getString(R.string.permission_call_phone));
                            break;

                    }
                    break;

                case Manifest.permission.ACCESS_FINE_LOCATION:
                    switch (status) {
                        case PackageManager.PERMISSION_GRANTED:
                            break;

                        case PackageManager.PERMISSION_DENIED:
                            controller.grantPermission(permission, context.getString(R.string.permission_call_phone));
                            break;

                    }
                    break;

            }
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
