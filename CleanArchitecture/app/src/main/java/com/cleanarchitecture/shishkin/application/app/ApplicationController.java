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
import com.cleanarchitecture.shishkin.base.controller.IActivityController;
import com.cleanarchitecture.shishkin.base.controller.IEventController;
import com.cleanarchitecture.shishkin.base.controller.ILifecycleController;
import com.cleanarchitecture.shishkin.base.controller.IMailController;
import com.cleanarchitecture.shishkin.base.controller.INavigationController;
import com.cleanarchitecture.shishkin.base.controller.IPresenterController;
import com.cleanarchitecture.shishkin.base.controller.LifecycleController;
import com.cleanarchitecture.shishkin.base.controller.MailController;
import com.cleanarchitecture.shishkin.base.controller.NavigationController;
import com.cleanarchitecture.shishkin.base.controller.PresenterController;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseOnLowMemoryEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseOnScreenOffEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseOnScreenOnEvent;
import com.cleanarchitecture.shishkin.base.repository.IRepository;
import com.cleanarchitecture.shishkin.base.repository.Repository;
import com.cleanarchitecture.shishkin.base.usecases.IUseCasesController;
import com.cleanarchitecture.shishkin.base.usecases.UseCasesController;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;
import com.github.snowdream.android.util.FilePathGenerator;
import com.github.snowdream.android.util.Log;
import com.squareup.leakcanary.LeakCanary;

import java.io.File;

public class ApplicationController extends Application {
    private static volatile ApplicationController sInstance;
    private static final String LOG_TAG = "ApplicationController";
    private static final long MAX_LOG_LENGTH = 2000000;//2Mb
    public static final String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS};

    private IEventController mEventController;
    private CrashController mCrashController;
    private IActivityController mActivityController;
    private ILifecycleController mLifecycleController;
    private IPresenterController mPresenterController;
    private INavigationController mNavigationController;
    private IUseCasesController mUseCasesController;
    private IRepository mRepository;
    private IMailController mMailController;

    @Override
    public void onCreate() {
        sInstance = this;

        super.onCreate();

        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }

        init();
    }

    public static synchronized ApplicationController getInstance() {
        if (sInstance == null) {
            Log.e(LOG_TAG, "Application is null");
        }
        return sInstance;
    }

    private void init() {

        mEventController = new EventController();
        mCrashController = new CrashController();
        mActivityController = new ActivityController();
        mLifecycleController = new LifecycleController();
        mPresenterController = new PresenterController();
        mNavigationController = new NavigationController();
        mUseCasesController = new UseCasesController();
        mRepository = new Repository();
        mMailController = new MailController();

        boolean isGrant = true;
        if (!ApplicationUtils.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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

        registerScreenOnOffBroadcastReceiver();

        // создаем БД
        new CreateDbTask().execute();

        Log.i(LOG_TAG, "Application inited");
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
                    getEventController().post(new UseCaseOnScreenOffEvent());
                } else {
                    getEventController().post(new UseCaseOnScreenOnEvent());
                }
            }
        };

        registerReceiver(screenOnOffReceiver, intentFilter);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        Log.e(LOG_TAG, "Low memory");
        getEventController().post(new UseCaseOnLowMemoryEvent());
    }

    public synchronized <C> C getController(final String controllerName) {
        switch (controllerName) {
            case ActivityController.NAME:
                return (C) mActivityController;

            case EventController.NAME:
                return (C) mEventController;

            case CrashController.NAME:
                return (C) mCrashController;

            case LifecycleController.NAME:
                return (C) mLifecycleController;

            case PresenterController.NAME:
                return (C) mPresenterController;

            case NavigationController.NAME:
                return (C) mNavigationController;

            case UseCasesController.NAME:
                return (C) mUseCasesController;

            case Repository.NAME:
                return (C) mRepository;

            case MailController.NAME:
                return (C) mMailController;
        }
        return null;
    }

    public IActivityController getActivityController() {
        return mActivityController;
    }

    public IEventController getEventController() {
        return mEventController;
    }

    public ILifecycleController getLifecycleController() {
        return mLifecycleController;
    }

    public IPresenterController getPresenterController() {
        return mPresenterController;
    }

    public INavigationController getNavigationController() {
        return mNavigationController;
    }

    public IUseCasesController getUseCasesController() {
        return mUseCasesController;
    }

    public IRepository getRepository() {
        return mRepository;
    }

    public IMailController getMailController() {
        return mMailController;
    }

}
