package com.cleanarchitecture.shishkin.base.usecases;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.controller.ActivityController;
import com.cleanarchitecture.shishkin.base.controller.AppPreferences;
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

        final int grant = AppPreferences.getInstance().getInt(context, permission, -111);
        AppPreferences.getInstance().putInt(context, permission, ApplicationUtils.getPermission(permission));
        switch (permission) {
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                switch (grant) {
                    case -111:
                        Log.setLog2FileEnabled(false);
                        if (ApplicationUtils.getPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                            ActivityController.getInstance().grantPermission(permission, ApplicationController.getInstance().getString(R.string.permission_write_external_storage));
                        } else {
                            UseCasesController.getInstance().setSystemDialogShown(false);
                            Log.setLog2FileEnabled(true);
                        }
                        break;

                    case PackageManager.PERMISSION_GRANTED:
                        Log.setLog2FileEnabled(true);
                        UseCasesController.getInstance().setSystemDialogShown(false);
                        break;

                    case PackageManager.PERMISSION_DENIED:
                        Log.setLog2FileEnabled(false);
                        int dialog = AppPreferences.getInstance().getInt(context, "dialog." + permission, 0);
                        if (dialog == 0) {
                            dialog++;
                            AppPreferences.getInstance().putInt(context, "dialog." + permission, dialog);
                            ActivityController.getInstance().grantPermission(permission, ApplicationController.getInstance().getString(R.string.permission_write_external_storage));
                        } else {
                            UseCasesController.getInstance().setSystemDialogShown(false);
                        }
                        break;

                }
                break;

            case Manifest.permission.READ_CONTACTS:
                switch (grant) {
                    case -111:
                        if (ApplicationUtils.getPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                            ActivityController.getInstance().grantPermission(permission, ApplicationController.getInstance().getString(R.string.permission_read_contacts));
                        } else {
                            UseCasesController.getInstance().setSystemDialogShown(false);
                        }
                        break;

                    case PackageManager.PERMISSION_GRANTED:
                        UseCasesController.getInstance().setSystemDialogShown(false);
                        break;

                    case PackageManager.PERMISSION_DENIED:
                        ActivityController.getInstance().grantPermission(permission, ApplicationController.getInstance().getString(R.string.permission_read_contacts));
                        break;

                }
                break;
            case Manifest.permission.CALL_PHONE:
                switch (grant) {
                    case -111:
                        if (ApplicationUtils.getPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                            ActivityController.getInstance().grantPermission(permission, ApplicationController.getInstance().getString(R.string.permission_call_phone));
                        } else {
                            UseCasesController.getInstance().setSystemDialogShown(false);
                        }
                        break;

                    case PackageManager.PERMISSION_GRANTED:
                        UseCasesController.getInstance().setSystemDialogShown(false);
                        break;

                    case PackageManager.PERMISSION_DENIED:
                        UseCasesController.getInstance().setSystemDialogShown(false);
                        break;

                }
                break;
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
