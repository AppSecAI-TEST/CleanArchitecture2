package com.cleanarchitecture.shishkin.application.app;

import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.cleanarchitecture.shishkin.base.controller.Admin;
import com.cleanarchitecture.shishkin.base.controller.IModule;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseOnLowMemoryEvent;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;

import java.io.File;

public class ApplicationController extends MultiDexApplication implements IModule {
    private static final String NAME = "ApplicationController";
    public static final String EXTERNAL_STORAGE_APPLICATION_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator + BuildConfig.APPLICATION_ID + File.separator;
    private static volatile ApplicationController sInstance;

    @Override
    public void onCreate() {
        sInstance = this;

        super.onCreate();

        //if (!LeakCanary.isInAnalyzerProcess(this)) {
        //    LeakCanary.install(this);
        //}

        Admin.instantiate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static ApplicationController getInstance() {
        return sInstance;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        ApplicationUtils.postEvent(new UseCaseOnLowMemoryEvent());
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return null;
    }
}
