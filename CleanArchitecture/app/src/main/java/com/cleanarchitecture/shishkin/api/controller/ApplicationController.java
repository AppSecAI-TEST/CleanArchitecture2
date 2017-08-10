package com.cleanarchitecture.shishkin.api.controller;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseOnLowMemoryEvent;
import com.cleanarchitecture.shishkin.api.handler.ApplicationLifecycleHandler;
import com.cleanarchitecture.shishkin.api.storage.ImageDiskCache;
import com.cleanarchitecture.shishkin.api.storage.ParcelableDiskCache;
import com.cleanarchitecture.shishkin.api.storage.SerializableDiskCache;

import java.io.File;

public class ApplicationController extends MultiDexApplication implements IApplicationController {

    public static final String NAME = ApplicationController.class.getName();
    private static final String EXTERNAL_STORAGE_APPLICATION_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator + BuildConfig.APPLICATION_ID + File.separator;
    private static String[] PERMISIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION};
    private static volatile ApplicationController sInstance;
    private static volatile ApplicationLifecycleHandler mHandler;

    @Override
    public void onCreate() {
        sInstance = this;

        super.onCreate();

        Admin.instantiate();

        mHandler = new ApplicationLifecycleHandler();
        registerActivityLifecycleCallbacks(mHandler);
        registerComponentCallbacks(mHandler);
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
    public synchronized void onApplicationUpdated(final int version) {
        SerializableDiskCache.getInstance(this).clear();
        ImageDiskCache.getInstance().clear();
        ParcelableDiskCache.getInstance().clear();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        AdminUtils.postEvent(new UseCaseOnLowMemoryEvent());
    }

    @Override
    public String getCachePath() {
        return getExternalCacheDir().getAbsolutePath();
    }

    @Override
    public String getDataPath() {
        return getFilesDir().getAbsolutePath() + File.separator;
    }

    @Override
    public String getExternalDataPath() {
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
    public boolean isPersistent() {
        return true;
    }

    @Override
    public void onUnRegisterModule() {
    }

    public boolean isInBackground() {
        return mHandler.isInBackground();
    }

    @Override
    public int getVersion() {
        try {
            final PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionCode;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String getDescription() {
        return getString(R.string.module_application);
    }

}
