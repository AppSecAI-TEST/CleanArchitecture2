package com.cleanarchitecture.shishkin.api.controller;

import android.Manifest;
import android.content.Context;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.data.ExtError;
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

    public static final int ERROR_LOST_APPLICATION_CONTEXT = 1;
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
        try {
            Log.setEnabled(true);
            Log.setLog2FileEnabled(true);
            String path;
            if (BuildConfig.DEBUG && AdminUtils.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                path = ApplicationController.getInstance().getExternalDataPath();
            } else {
                path = ApplicationController.getInstance().getDataPath();
            }
            final File file = new File(path);
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

        if (!StringUtils.isNullOrEmpty(displayMessage)) {
            AdminUtils.postEvent(new ShowErrorMessageEvent(displayMessage + getSufix()));
        }
    }

    @Override
    public synchronized void onError(final String source, final Exception e, final int errorCode) {
        onError(source, e);

        if (errorCode != 0) {
            AdminUtils.postEvent(new ShowErrorMessageEvent(AdminUtils.getErrorText(errorCode) + getSufix()));
        }
    }

    @Override
    public synchronized void onError(final String source, final String message, final boolean isDisplay) {
        if (StringUtils.isNullOrEmpty(message)) {
            Log.e(source, message);
            if (isDisplay) {
                AdminUtils.postEvent(new ShowErrorMessageEvent(message + getSufix()));
            }
        }
    }

    @Override
    public synchronized void onError(final String source, final int errorCode, final boolean isDisplay) {
        if (errorCode != 0) {
            Log.e(source, AdminUtils.getErrorText(errorCode));
            if (isDisplay) {
                AdminUtils.postEvent(new ShowErrorMessageEvent(AdminUtils.getErrorText(errorCode) + getSufix()));
            }
        }
    }

    @Override
    public synchronized void onError(final ExtError extError) {
        if (extError != null && extError.hasError()) {
            AdminUtils.postEvent(new ShowErrorMessageEvent(extError.getErrorText() + getSufix()));
        }
    }

    @Override
    public synchronized String getPath() {
        return Log.getPath();
    }

    @Override
    public synchronized void clearLog() {
        final File log = new File(getPath());
        if (log.exists()) {
            log.delete();
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public void onUnRegister() {
    }

    private String getSufix() {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            return context.getString(R.string.error_sufix);
        }
        return "";
    }

    @Override
    public String getDescription() {
        final Context context = ApplicationController.getInstance();
        if (context != null) {
            return context.getString(R.string.module_error);
        }
        return "Error controller";
    }

}
