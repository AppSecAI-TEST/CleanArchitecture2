package com.cleanarchitecture.shishkin.application.app;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;
import com.github.snowdream.android.util.FilePathGenerator;
import com.github.snowdream.android.util.Log;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

public class ApplicationController extends Application {
    private static volatile ApplicationController sInstance;
    private static final String LOG_TAG = "ApplicationController";
    private static final long MAX_LOG_LENGTH = 2000000;//2Mb
    public static String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private ReentrantLock mLock;

    @Override
    public void onCreate() {

        super.onCreate();

        sInstance = this;

        mLock = new ReentrantLock();

        init();
    }

    public static synchronized ApplicationController getInstance() {
        if (sInstance == null) {
            Log.e(LOG_TAG, "Application is null");
        }
        return sInstance;
    }

    public void init() {

        mLock.lock();

        try {
            boolean isGrant = true;
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    isGrant = false;
                }
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
                                getString(R.string.app_name), ".log"));
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


        } catch (Exception e) {
            android.util.Log.e(getClass().getSimpleName(), e.getMessage());
        } finally {
            mLock.unlock();
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
        }
    }

}
