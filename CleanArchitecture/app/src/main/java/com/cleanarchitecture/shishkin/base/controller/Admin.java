package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.utils.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class Admin implements ISubscriber {
    public static final String NAME = "Controllers";

    private static volatile Admin sInstance;
    private Map<String, IModule> mModules;
    private Map<String, String> mTypeModules;

    public static Admin getInstance() {
        if (sInstance == null) {
            synchronized (Admin.class) {
                if (sInstance == null) {
                    sInstance = new Admin();
                }
            }
        }
        return sInstance;
    }

    private Admin() {
        mModules = Collections.synchronizedMap(new HashMap<String, IModule>());
        mTypeModules = Collections.synchronizedMap(new HashMap<String, String>());
    }

    public synchronized <C> C getModule(final String controllerName) {
        if (StringUtils.isNullOrEmpty(controllerName)) {
            return null;
        }

        try {
            if (mModules.containsKey(controllerName)) {
                return (C) mModules.get(controllerName);
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(NAME, e);
        }
        return null;
    }

    public synchronized void registerModule(final IModule controller) {
        if (controller != null) {
            mModules.put(controller.getName(), controller);
            mTypeModules.put(controller.getSubscriberType(), getName());
        }
    }

    public synchronized void unregisterModule(final String nameController) {
        if (!StringUtils.isNullOrEmpty(nameController)) {
            if (mModules.containsKey(nameController)) {
                for (IModule module : mModules.values()) {
                    if (nameController.equalsIgnoreCase(module.getName())) {
                        mTypeModules.remove(module.getSubscriberType());
                    }
                }

                mModules.remove(nameController);
            }
        }
    }

    public synchronized void register(final IModuleSubscriber subscriber) {
        if (subscriber != null) {
            try {
                final List<String> types = subscriber.hasSubscriberType();

                // регистрируемся subscriber в чужих моулях
                for (IModule module : mModules.values()) {
                    if (module instanceof IController) {
                        if (types.contains(module.getSubscriberType())) {
                            ((IController) module).register(subscriber);
                        }
                    }
                }

                // регистрируем чужие модули у subscriber
                if (subscriber instanceof IController) {
                    final String type = ((IController) subscriber).getSubscriberType();
                    for (IModule module : mModules.values()) {
                        if (module instanceof IModuleSubscriber) {
                            if (((IModuleSubscriber) module).hasSubscriberType().contains(type)) {
                                ((IController) subscriber).register(module);
                            }
                        }
                    }

                }
            } catch (Exception e) {
                ErrorController.getInstance().onError(NAME, e.getMessage());
            }
        }
    }

    public synchronized void unregister(final IModuleSubscriber subscriber) {
        if (subscriber != null) {
            try {
                final List<String> types = subscriber.hasSubscriberType();
                for (IModule module : mModules.values()) {
                    if (module instanceof IController) {
                        if (types.contains(module.getSubscriberType())) {
                            ((IController) module).unregister(subscriber);
                        }
                    }
                }
            } catch (Exception e) {
                ErrorController.getInstance().onError(NAME, e.getMessage());
            }
        }
    }

    public synchronized void setCurrentSubscriber(final IModuleSubscriber subscriber) {
        if (subscriber != null) {
            try {
                final List<String> types = subscriber.hasSubscriberType();
                for (IModule module : mModules.values()) {
                    if (module instanceof IController) {
                        if (types.contains(module.getSubscriberType())) {
                            ((IController) module).setCurrentSubscriber(subscriber);
                        }
                    }
                }
            } catch (Exception e) {
                ErrorController.getInstance().onError(NAME, e.getMessage());
            }
        }
    }



    @Override
    public String getName() {
        return NAME;
    }
}
