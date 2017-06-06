package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.utils.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public abstract class AbstractAdmin implements IAdmin {
    private static final String NAME = "AbstractAdmin";

    private Map<String, IModule> mModules;

    public AbstractAdmin() {
        mModules = Collections.synchronizedMap(new HashMap<String, IModule>());
    }

    @Override
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

    @Override
    public synchronized void registerModule(final IModule controller) {
        if (controller != null) {
            try {
                // регистрируем модуль в других модулях
                if (controller instanceof IModuleSubscriber) {
                    final List<String> types = ((IModuleSubscriber) controller).hasSubscriberType();

                    for (IModule module : mModules.values()) {
                        if (module instanceof IController) {
                            if (types.contains(module.getSubscriberType())) {
                                ((IController) module).register(controller);
                            }
                        }
                    }
                }

                // регистрируем другие модули в модуле
                if (controller instanceof IController) {
                    final String type = controller.getSubscriberType();
                    for (IModule module : mModules.values()) {
                        if (module instanceof IModuleSubscriber) {
                            if (((IModuleSubscriber) module).hasSubscriberType().contains(type)) {
                                ((IController) controller).register(module);
                            }
                        }
                    }
                }

                mModules.put(controller.getName(), controller);
            } catch (Exception e) {
                ErrorController.getInstance().onError(NAME, e.getMessage());
            }
        }
    }

    @Override
    public synchronized void unregisterModule(final String nameController) {
        if (!StringUtils.isNullOrEmpty(nameController)) {
            try {
                // отменяем регистрацию в других модулях
                if (mModules.containsKey(nameController)) {
                    final IModule module = mModules.get(nameController);
                    if (module != null && module instanceof IModuleSubscriber) {
                        final List<String> subscribers = ((IModuleSubscriber) module).hasSubscriberType();
                        for (String subscriber : subscribers) {
                            final IModule moduleSubscriber = mModules.get(subscriber);
                            if (moduleSubscriber != null && moduleSubscriber instanceof IController) {
                                ((IController) moduleSubscriber).unregister(module);
                            }
                        }
                    }

                    mModules.remove(nameController);
                }
            } catch (Exception e) {
                ErrorController.getInstance().onError(NAME, e.getMessage());
            }
        }
    }

    @Override
    public synchronized void register(final IModuleSubscriber subscriber) {
        if (subscriber != null) {
            try {
                final List<String> types = subscriber.hasSubscriberType();

                // регистрируемся subscriber в модулях
                for (IModule module : mModules.values()) {
                    if (module instanceof IController) {
                        if (types.contains(module.getSubscriberType())) {
                            ((IController) module).register(subscriber);
                        }
                    }
                }
            } catch (Exception e) {
                ErrorController.getInstance().onError(NAME, e.getMessage());
            }
        }
    }

    @Override
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

    @Override
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
}
