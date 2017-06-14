package com.cleanarchitecture.shishkin.api.controller;

import android.content.Context;

import com.cleanarchitecture.shishkin.api.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.api.repository.ContentProvider;
import com.cleanarchitecture.shishkin.api.repository.DbProvider;
import com.cleanarchitecture.shishkin.api.repository.NetProvider;
import com.cleanarchitecture.shishkin.api.repository.Repository;
import com.cleanarchitecture.shishkin.api.storage.DiskCache;
import com.cleanarchitecture.shishkin.api.storage.MemoryCache;
import com.cleanarchitecture.shishkin.api.usecases.UseCasesController;
import com.cleanarchitecture.shishkin.application.app.ApplicationController;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@SuppressWarnings("unused")
public class Admin extends AbstractAdmin {
    public static final String NAME = "Admin";

    private static volatile Admin sInstance;

    private boolean isFinishApplication = false;

    public static void instantiate() {
        if (sInstance == null) {
            synchronized (Admin.class) {
                if (sInstance == null) {
                    sInstance = new Admin();
                }
            }
        }
    }

    public static Admin getInstance() {
        if (sInstance == null) {
            instantiate();
        }
        return sInstance;
    }

    private Admin() {
        EventBusController.getInstance().register(this);

        register();
    }

    @Override
    public String getName() {
        return NAME;
    }

    public synchronized void unregister() {
        unregister(DesktopController.NAME);
        unregister(Repository.NAME);
        unregister(NetProvider.NAME);
        unregister(DbProvider.NAME);
        unregister(ContentProvider.NAME);
        unregister(UserIteractionController.NAME);
        unregister(MailController.NAME);
        unregister(UseCasesController.NAME);
        unregister(NavigationController.NAME);
        unregister(PresenterController.NAME);
        unregister(LifecycleController.NAME);
        unregister(ActivityController.NAME);
        unregister(CrashController.NAME);
    }

    public synchronized void register() {
        isFinishApplication = false;

        final Context context = ApplicationController.getInstance();

        // default Singleton controllers
        registerModule(ErrorController.getInstance());
        registerModule(EventBusController.getInstance());
        registerModule(MemoryCache.getInstance());
        if (context != null) {
            registerModule(DiskCache.getInstance(context));
        }

        // other controllers
        registerModule(CrashController.NAME);
        registerModule(ActivityController.NAME);
        registerModule(LifecycleController.NAME);
        registerModule(PresenterController.NAME);
        registerModule(NavigationController.NAME);
        registerModule(UseCasesController.NAME);
        registerModule(MailController.NAME);
        registerModule(UserIteractionController.NAME);
        registerModule(ContentProvider.NAME);
        registerModule(DbProvider.NAME);
        registerModule(NetProvider.NAME);
        registerModule(Repository.NAME);
        registerModule(DesktopController.NAME);

    }

    public boolean isFinishApplication() {
        return isFinishApplication;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onFinishApplicationEvent(FinishApplicationEvent event) {
        isFinishApplication = true;
    }

}
