package com.cleanarchitecture.shishkin.application.app;

import android.Manifest;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.task.CreateDbTask;
import com.cleanarchitecture.shishkin.base.controller.ActivityController;
import com.cleanarchitecture.shishkin.base.controller.CrashController;
import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.controller.LifecycleController;
import com.cleanarchitecture.shishkin.base.controller.MailController;
import com.cleanarchitecture.shishkin.base.controller.NavigationController;
import com.cleanarchitecture.shishkin.base.controller.PresenterController;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseOnLowMemoryEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseOnScreenOffEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseOnScreenOnEvent;
import com.cleanarchitecture.shishkin.base.repository.Repository;
import com.cleanarchitecture.shishkin.base.usecases.UseCasesController;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;
import com.github.snowdream.android.util.FilePathGenerator;
import com.github.snowdream.android.util.Log;

import java.io.File;

public class ApplicationController extends Application {
    private static volatile ApplicationController sInstance;
    private static final String LOG_TAG = "ApplicationController";
    private static final long MAX_LOG_LENGTH = 2000000;//2Mb
    public static final String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public void onCreate() {

        super.onCreate();

        sInstance = this;

        init();
    }

    public static synchronized ApplicationController getInstance() {
        if (sInstance == null) {
            Log.e(LOG_TAG, "Application is null");
        }
        return sInstance;
    }

    public synchronized void init() {
        boolean isGrant = true;
        if (ApplicationUtils.hasMarshmallow()) {
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

        EventController.instantiate();
        CrashController.instantiate();
        ActivityController.instantiate();
        LifecycleController.instantiate();
        PresenterController.instantiate();
        NavigationController.instantiate();
        UseCasesController.instantiate();
        Repository.instantiate();
        MailController.instantiate();

        registerScreenOnOffBroadcastReceiver();

        // создаем БД
        new CreateDbTask().execute();
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
            android.util.Log.e(LOG_TAG, e.getMessage());
        }
    }

    public void registerScreenOnOffBroadcastReceiver() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        final BroadcastReceiver screenOnOffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String strAction = intent.getAction();

                if (strAction.equals(Intent.ACTION_SCREEN_OFF)) {
                    EventController.getInstance().post(new UseCaseOnScreenOffEvent());
                } else {
                    EventController.getInstance().post(new UseCaseOnScreenOnEvent());
                }
            }
        };

        registerReceiver(screenOnOffReceiver, intentFilter);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        EventController.getInstance().post(new UseCaseOnLowMemoryEvent());
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            EventController.getInstance().post(new UseCaseOnLowMemoryEvent());
        }
    }


}
