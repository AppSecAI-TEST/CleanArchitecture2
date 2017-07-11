package com.cleanarchitecture.shishkin.api.controller;

import android.content.Context;

import com.cleanarchitecture.shishkin.api.repository.ContentProvider;
import com.cleanarchitecture.shishkin.api.repository.ContentProviderProxy;
import com.cleanarchitecture.shishkin.api.repository.DbProvider;
import com.cleanarchitecture.shishkin.api.repository.NetProvider;
import com.cleanarchitecture.shishkin.api.repository.Repository;
import com.cleanarchitecture.shishkin.api.storage.ParcelableDiskCache;
import com.cleanarchitecture.shishkin.api.storage.ParcelableMemoryCache;
import com.cleanarchitecture.shishkin.api.storage.SerializableDiskCache;
import com.cleanarchitecture.shishkin.api.storage.SerializableMemoryCache;
import com.cleanarchitecture.shishkin.api.usecases.UseCasesController;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;

@SuppressWarnings("unused")
public class Admin extends AbstractAdmin {
    public static final String NAME = Admin.class.getName();
    private static final int MIN_HEAP_SIZE = 48;

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
        // default persistent (Singleton) controllers
        registerModule(ApplicationController.getInstance());
        registerModule(ErrorController.getInstance());
        registerModule(EventBusController.getInstance());
        registerModule(AppPreferencesModule.getInstance());

        // default persistent (Singleton) cache controllers
        if (ApplicationUtils.getHeapSize() > MIN_HEAP_SIZE) {
            registerModule(SerializableMemoryCache.getInstance());
        }
        if (ApplicationUtils.getHeapSize() > MIN_HEAP_SIZE) {
            registerModule(ParcelableMemoryCache.getInstance());
        }

        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            registerModule(SerializableDiskCache.getInstance(context));
        }
        registerModule(ParcelableDiskCache.getInstance());

        // other controllers
        registerModule(CrashController.NAME);
        registerModule(ActivityController.NAME);
        registerModule(PresenterController.NAME);
        registerModule(NavigationController.NAME);
        registerModule(UseCasesController.NAME);
        registerModule(MailController.NAME);
        registerModule(UserIteractionController.NAME);
        registerModule(ContentProvider.NAME);
        registerModule(ContentProviderProxy.NAME);
        registerModule(DbProvider.NAME);
        registerModule(NetProvider.NAME);
        registerModule(Repository.NAME);
        registerModule(DesktopController.NAME);
        registerModule(LocationController.NAME);
        registerModule(TransformDataModule.NAME);
        registerModule(AppPreferencesModule.NAME);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
