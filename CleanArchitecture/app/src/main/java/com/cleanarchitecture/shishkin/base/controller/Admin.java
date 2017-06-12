package com.cleanarchitecture.shishkin.base.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.cleanarchitecture.shishkin.IAidlInterface;
import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.aidl.AidlService;
import com.cleanarchitecture.shishkin.base.event.ui.ShowToastEvent;
import com.cleanarchitecture.shishkin.base.repository.IRepository;
import com.cleanarchitecture.shishkin.base.repository.Repository;
import com.cleanarchitecture.shishkin.base.storage.DiskCache;
import com.cleanarchitecture.shishkin.base.storage.MemoryCache;
import com.cleanarchitecture.shishkin.base.usecases.UseCasesController;
import com.cleanarchitecture.shishkin.base.utils.AdminUtils;

@SuppressWarnings("unused")
public class Admin extends AbstractAdmin {
    public static final String NAME = "Admin";

    private static volatile Admin sInstance;
    private IAidlInterface mService;
    private ServiceConnection mConnection;

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

        mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                mService = IAidlInterface.Stub.asInterface(service);
                Admin.getInstance().registerObject(AidlService.NAME, mService);
            }

            public void onServiceDisconnected(ComponentName className) {
                mService = null;
                Admin.getInstance().unregister(AidlService.NAME);
            }
        };

        if (context != null) {
            final Intent intent = new Intent();
            intent.setClassName(context.getPackageName(), AidlService.class.getName());
            intent.setAction(IAidlInterface.class.getName());
            context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    public String getName() {
        return NAME;
    }

}
