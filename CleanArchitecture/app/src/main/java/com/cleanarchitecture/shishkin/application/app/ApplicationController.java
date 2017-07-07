package com.cleanarchitecture.shishkin.application.app;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.cleanarchitecture.shishkin.api.controller.Admin;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseOnLowMemoryEvent;

import java.io.File;

public class ApplicationController extends MultiDexApplication {

    public static final String NAME = ApplicationController.class.getName();
    public static final String EXTERNAL_STORAGE_APPLICATION_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator + BuildConfig.APPLICATION_ID + File.separator;
    public static String[] PERMISIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION};
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

        AdminUtils.postEvent(new UseCaseOnLowMemoryEvent());
    }

    public String getExternalCachePath() {
        return getExternalCacheDir().getAbsolutePath();
    }

}
