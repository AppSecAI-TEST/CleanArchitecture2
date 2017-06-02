package com.cleanarchitecture.shishkin.base.controller;

import android.arch.persistence.room.RoomDatabase;

import com.cleanarchitecture.shishkin.base.repository.IContentProvider;
import com.cleanarchitecture.shishkin.base.repository.IDbProvider;
import com.cleanarchitecture.shishkin.base.repository.INetProvider;
import com.cleanarchitecture.shishkin.base.repository.IRepository;
import com.cleanarchitecture.shishkin.base.repository.Repository;
import com.cleanarchitecture.shishkin.base.usecases.IUseCasesController;
import com.cleanarchitecture.shishkin.base.usecases.UseCasesController;
import com.cleanarchitecture.shishkin.base.utils.StringUtils;
import com.github.snowdream.android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class Controllers implements ISubscriber {
    public static final String NAME = "Controllers";

    private static volatile Controllers sInstance;

    private Map<String, Object> mMap;

    public static void instantiate() {
        if (sInstance == null) {
            synchronized (Controllers.class) {
                if (sInstance == null) {
                    sInstance = new Controllers();
                }
            }
        }
    }

    public static Controllers getInstance() {
        if (sInstance == null) {
            instantiate();
        }
        return sInstance;
    }

    private Controllers() {
        mMap = Collections.synchronizedMap(new HashMap<String, Object>());

        register(this);
        register(EventBusController.getInstance());
        register(new CrashController());
        register(new ActivityController());
        register(new LifecycleController());
        register(new PresenterController());
        register(new NavigationController());
        register(new UseCasesController());
        register(new Repository());
        register(new MailController());

        register(getRepository().getDbProvider());
        register(getRepository().getNetProvider());
        register(getRepository().getContentProvider());
    }

    public synchronized <C> C getController(final String controllerName) {
        if (StringUtils.isNullOrEmpty(controllerName)) {
            return null;
        }

        try {
            if (mMap.containsKey(controllerName)) {
                return (C) mMap.get(controllerName);
            }
        } catch (Exception e) {
            Log.e(NAME, e.getMessage());
        }
        return null;
    }

    public synchronized void register(final String nameController, final Object controller) {
        if (!StringUtils.isNullOrEmpty(nameController) && controller != null) {
            mMap.put(nameController, controller);
        }
    }

    public synchronized void register(final ISubscriber controller) {
        if (controller != null) {
            mMap.put(controller.getName(), controller);
        }
    }

    public synchronized void unregister(final String nameController) {
        if (!StringUtils.isNullOrEmpty(nameController)) {
            if (mMap.containsKey(nameController)) {
                mMap.remove(nameController);
            }
        }
    }

    public synchronized IActivityController getActivityController() {
        return getController(ActivityController.NAME);
    }

    public synchronized ILifecycleController getLifecycleController() {
        return getController(LifecycleController.NAME);
    }

    public synchronized IPresenterController getPresenterController() {
        return getController(PresenterController.NAME);
    }

    public synchronized INavigationController getNavigationController() {
        return getController(NavigationController.NAME);
    }

    public synchronized IUseCasesController getUseCasesController() {
        return getController(UseCasesController.NAME);
    }

    public synchronized IRepository getRepository() {
        return getController(Repository.NAME);
    }

    public synchronized IMailController getMailController() {
        return getController(MailController.NAME);
    }

    public synchronized IDbProvider getDbProvider() {
        final IRepository repository = getRepository();
        if (repository != null) {
            return repository.getDbProvider();
        }
        return null;
    }

    public synchronized INetProvider getNetProvider() {
        final IRepository repository = getRepository();
        if (repository != null) {
            return repository.getNetProvider();
        }
        return null;
    }

    public synchronized IContentProvider getContentProvider() {
        final IRepository repository = getRepository();
        if (repository != null) {
            return repository.getContentProvider();
        }
        return null;
    }

    public synchronized <T extends RoomDatabase> T getDb(final Class<T> klass, final String databaseName) {
        final IRepository repository = getRepository();
        if (repository != null) {
            final IDbProvider provider = repository.getDbProvider();
            if (provider != null) {
                return provider.getDb(klass, databaseName);
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
