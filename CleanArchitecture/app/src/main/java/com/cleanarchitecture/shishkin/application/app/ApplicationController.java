package com.cleanarchitecture.shishkin.application.app;

import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.cleanarchitecture.shishkin.base.controller.ActivityController;
import com.cleanarchitecture.shishkin.base.controller.Admin;
import com.cleanarchitecture.shishkin.base.controller.CrashController;
import com.cleanarchitecture.shishkin.base.controller.ErrorController;
import com.cleanarchitecture.shishkin.base.controller.EventBusController;
import com.cleanarchitecture.shishkin.base.controller.IModule;
import com.cleanarchitecture.shishkin.base.controller.LifecycleController;
import com.cleanarchitecture.shishkin.base.controller.MailController;
import com.cleanarchitecture.shishkin.base.controller.NavigationController;
import com.cleanarchitecture.shishkin.base.controller.PresenterController;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseOnLowMemoryEvent;
import com.cleanarchitecture.shishkin.base.repository.IRepository;
import com.cleanarchitecture.shishkin.base.repository.Repository;
import com.cleanarchitecture.shishkin.base.usecases.UseCasesController;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;
import com.squareup.leakcanary.LeakCanary;

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

        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }

        Admin.getInstance().registerModule(ErrorController.getInstance());
        Admin.getInstance().registerModule(EventBusController.getInstance());
        Admin.getInstance().registerModule(new CrashController());
        Admin.getInstance().registerModule(new ActivityController());
        Admin.getInstance().registerModule(new LifecycleController());
        Admin.getInstance().registerModule(new PresenterController());
        Admin.getInstance().registerModule(new NavigationController());
        Admin.getInstance().registerModule(new UseCasesController());
        Admin.getInstance().registerModule(new MailController());
        Admin.getInstance().registerModule(this);

        final IRepository repository = new Repository();
        Admin.getInstance().registerModule(repository);
        Admin.getInstance().registerModule(repository.getDbProvider());
        Admin.getInstance().registerModule(repository.getNetProvider());
        Admin.getInstance().registerModule(repository.getContentProvider());
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
