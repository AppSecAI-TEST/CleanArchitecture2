package com.cleanarchitecture.shishkin.api.controller;

import android.Manifest;
import android.os.Environment;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.cleanarchitecture.shishkin.api.event.ui.ShowErrorMessageEvent;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;
import com.github.snowdream.android.util.FilePathGenerator;
import com.github.snowdream.android.util.Log;

import java.io.File;

/**
 * Контроллер ошибок
 */
public class ErrorController implements IErrorController {
    public static final String NAME = ErrorController.class.getName();
    private static final long MAX_LOG_LENGTH = 2000000;//2Mb

    private static volatile ErrorController sInstance;

    public static final int ERROR_LOST_AAPLICATION_CONTEXT = 1;
    public static final int ERROR_GET_DATA = 2;
    public static final int ERROR_DB = 3;
    public static final int ERROR_NOT_FOUND_ACTIVITY = 4;
    public static final int ERROR_ACTIVITY_NOT_VALID = 5;
    public static final int ERROR_GEOCODER_NOT_FOUND = 6;

    public static ErrorController getInstance() {
        if (sInstance == null) {
            synchronized (ErrorController.class) {
                if (sInstance == null) {
                    sInstance = new ErrorController();
                }
            }
        }
        return sInstance;
    }

    private ErrorController() {
        boolean isGrant = true;
        if (!AdminUtils.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            isGrant = false;
        }

        if (isGrant) {
            try {
                Log.setEnabled(true);
                Log.setLog2FileEnabled(true);
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + BuildConfig.APPLICATION_ID;
                final File file = new File(path + File.separator);
                if (!file.exists()) {
                    file.mkdirs();
                }
                if (file.exists()) {
                    Log.setFilePathGenerator(new FilePathGenerator.DefaultFilePathGenerator(path,
                            StringUtils.replace(BuildConfig.APPLICATION_ID, ".", "_"), ".log"));
                    checkLogSize();
                } else {
                    Log.setEnabled(false);
                }
            } catch (Exception e) {
                Log.setEnabled(false);
            }
        } else {
            Log.setEnabled(false);
        }
    }

    private void checkLogSize() {
        final String path = Log.getPath();

        try {
            final File file = new File(path);
            if (file.exists()) {
                if (file.length() == 0) {
                    Log.i(ApplicationUtils.getPhoneInfo());
                }

                if (file.length() > MAX_LOG_LENGTH) {
                    final String new_path = path + ".1";
                    final File new_file = new File(new_path);
                    if (new_file.exists()) {
                        new_file.delete();
                    }
                    file.renameTo(new_file);
                }
            }
        } catch (Exception e) {
            android.util.Log.e(NAME, e.getMessage());
        }
    }

    @Override
    public synchronized void onError(final String source, final Exception e) {
        if (BuildConfig.DEBUG) {
            Log.e(source, e);
        } else {
            Log.e(source, e.getMessage());
        }
    }

    @Override
    public synchronized void onError(final String source, final Throwable throwable) {
        if (BuildConfig.DEBUG) {
            Log.e(source, throwable);
        } else {
            Log.e(source, throwable.getMessage());
        }
    }

    @Override
    public synchronized void onError(final String source, final Exception e, final String displayMessage) {
        onError(source, e);

        AdminUtils.postEvent(new ShowErrorMessageEvent(displayMessage));
    }

    @Override
    public synchronized void onError(final String source, final Exception e, final int errorCode) {
        onError(source, e);

        AdminUtils.postEvent(new ShowErrorMessageEvent(errorCode));
    }

    @Override
    public synchronized void onError(final String source, final String message, final boolean isDisplay) {
        if (isDisplay) {
            AdminUtils.postEvent(new ShowErrorMessageEvent(message));
        } else {
            Log.e(source, message);
        }
    }

    @Override
    public synchronized void onError(final String source, final int errorCode, final boolean isDisplay) {
        if (isDisplay) {
            AdminUtils.postEvent(new ShowErrorMessageEvent(errorCode));
        } else {
            Log.e(source, AdminUtils.getErrorText(errorCode));
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

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public void onUnRegister() {
    }

}
