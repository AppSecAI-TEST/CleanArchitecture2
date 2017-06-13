package com.cleanarchitecture.shishkin.api.controller;

import android.content.Context;

import com.cleanarchitecture.shishkin.api.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.api.repository.ContentProvider;
import com.cleanarchitecture.shishkin.api.repository.DbProvider;
import com.cleanarchitecture.shishkin.api.repository.NetProvider;
import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.api.repository.IRepository;
import com.cleanarchitecture.shishkin.api.repository.Repository;
import com.cleanarchitecture.shishkin.api.storage.DiskCache;
import com.cleanarchitecture.shishkin.api.storage.MemoryCache;
import com.cleanarchitecture.shishkin.api.usecases.UseCasesController;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;

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

    @Override
    public synchronized void unregister() {
        unregister(ContentProvider.NAME);
        unregister(NetProvider.NAME);
        unregister(DbProvider.NAME);
        unregister(Repository.NAME);

        unregister(UserIteractionController.NAME);
        unregister(MailController.NAME);
        unregister(UseCasesController.NAME);
        unregister(NavigationController.NAME);
        unregister(PresenterController.NAME);
        unregister(LifecycleController.NAME);
        unregister(ActivityController.NAME);
        unregister(CrashController.NAME);

        unregister(DiskCache.NAME);
        unregister(MemoryCache.NAME);
    }

    @Override
    public synchronized void register() {
        isFinishApplication = false;

        final Context context = ApplicationController.getInstance();

        // default controllers
        if (!containsModule(ErrorController.NAME)) {
            registerModule(ErrorController.getInstance());
        }

        if (!containsModule(EventBusController.NAME)) {
            registerModule(EventBusController.getInstance());
        }

        // other controllers
        if (!containsModule(MemoryCache.NAME)) {
            registerModule(MemoryCache.getInstance());
        }
        if (!containsModule(DiskCache.NAME)) {
            if (context != null) {
                registerModule(DiskCache.getInstance(context));
            }
        }

        if (!containsModule(CrashController.NAME)) {
            registerModule(new CrashController());
        }
        if (!containsModule(ActivityController.NAME)) {
            registerModule(new ActivityController());
        }
        if (!containsModule(LifecycleController.NAME)) {
            registerModule(new LifecycleController());
        }
        if (!containsModule(PresenterController.NAME)) {
            registerModule(new PresenterController());
        }
        if (!containsModule(NavigationController.NAME)) {
            registerModule(new NavigationController());
        }
        if (!containsModule(UseCasesController.NAME)) {
            registerModule(new UseCasesController());
        }
        if (!containsModule(MailController.NAME)) {
            registerModule(new MailController());
        }
        if (!containsModule(UserIteractionController.NAME)) {
            registerModule(new UserIteractionController());
        }

        if (!containsModule(Repository.NAME)) {
            final IRepository repository = new Repository();
            registerModule(repository);
            registerModule(repository.getDbProvider());
            registerModule(repository.getNetProvider());
            registerModule(repository.getContentProvider());
        }

    }

    public boolean isFinishApplication() {
        return isFinishApplication;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onFinishApplicationEvent(FinishApplicationEvent event) {
        isFinishApplication = true;
    }

}
