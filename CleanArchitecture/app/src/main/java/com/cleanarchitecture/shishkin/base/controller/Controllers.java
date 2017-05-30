package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.repository.IRepository;
import com.cleanarchitecture.shishkin.base.repository.Repository;
import com.cleanarchitecture.shishkin.base.usecases.IUseCasesController;
import com.cleanarchitecture.shishkin.base.usecases.UseCasesController;
import com.cleanarchitecture.shishkin.base.utils.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Controllers {
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

        final IEventController controller = new EventController();
        register(EventController.NAME, controller);
        register(CrashController.NAME, new CrashController());
        register(ActivityController.NAME, new ActivityController(controller));
        register(LifecycleController.NAME, new LifecycleController(controller));
        register(PresenterController.NAME, new PresenterController());
        register(NavigationController.NAME, new NavigationController(controller));
        register(UseCasesController.NAME, new UseCasesController(controller));
        register(Repository.NAME, new Repository(controller));
        register(MailController.NAME, new MailController());
    }

    public synchronized <C> C getController(final String controllerName) {
        if (mMap.containsKey(controllerName)) {
            return (C) mMap.get(controllerName);
        }
        return null;
    }

    public synchronized void register(final String nameController, final Object controller) {
        if (!StringUtils.isNullOrEmpty(nameController) && controller != null) {
            mMap.put(nameController, controller);
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

    public synchronized IEventController getEventController() {
        IEventController mEventController = getController(EventController.NAME);
        return mEventController;
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




}
