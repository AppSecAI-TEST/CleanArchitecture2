package com.cleanarchitecture.shishkin.api.controller;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseOnLowMemoryEvent;

import java.io.File;

public class ApplicationController extends MultiDexApplication implements IApplicationController {

    public static final String NAME = ApplicationController.class.getName();
    private static final String EXTERNAL_STORAGE_APPLICATION_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator + BuildConfig.APPLICATION_ID + File.separator;
    private static String[] PERMISIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION};
    private static volatile ApplicationController sInstance;

    @Override
    public void onCreate() {
        sInstance = this;

        super.onCreate();

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

    @Override
    public String getExternalCachePath() {
        return getExternalCacheDir().getAbsolutePath();
    }

    @Override
    public String getExternalApplicationPath() {
        return EXTERNAL_STORAGE_APPLICATION_PATH;
    }

    @Override
    public String[] getRequiredPermisions() {
        return PERMISIONS;
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
        return false;
    }

    @Override
    public void onUnRegister() {

    }
}
