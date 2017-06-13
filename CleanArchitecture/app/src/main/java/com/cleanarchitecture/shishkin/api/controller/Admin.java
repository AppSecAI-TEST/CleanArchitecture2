package com.cleanarchitecture.shishkin.api.controller;

import android.content.Context;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.api.repository.IRepository;
import com.cleanarchitecture.shishkin.api.repository.Repository;
import com.cleanarchitecture.shishkin.api.storage.DiskCache;
import com.cleanarchitecture.shishkin.api.storage.MemoryCache;
import com.cleanarchitecture.shishkin.api.usecases.UseCasesController;

@SuppressWarnings("unused")
public class Admin extends AbstractAdmin {
    public static final String NAME = "Admin";

    private static volatile Admin sInstance;

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
        final Context context = ApplicationController.getInstance();

        registerModule(ErrorController.getInstance());
        registerModule(EventBusController.getInstance());
        registerModule(MemoryCache.getInstance());
        if (context != null) {
            registerModule(DiskCache.getInstance(context));
        }

        registerModule(new CrashController());
        registerModule(new ActivityController());
        registerModule(new LifecycleController());
        registerModule(new PresenterController());
        registerModule(new NavigationController());
        registerModule(new UseCasesController());
        registerModule(new MailController());
        registerModule(new UserIteractionController());

        final IRepository repository = new Repository();
        registerModule(repository);
        registerModule(repository.getDbProvider());
        registerModule(repository.getNetProvider());
        registerModule(repository.getContentProvider());
    }

    @Override
    public String getName() {
        return NAME;
    }

}
