package com.cleanarchitecture.shishkin.api.controller;

import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public abstract class AbstractAdmin implements IAdmin {
    private static final String NAME = AbstractAdmin.class.getName();
    private static final String LOG_TAG = "AbstractAdmin:";

    private Map<String, IModule> mModules = Collections.synchronizedMap(new ConcurrentHashMap<String, IModule>());
    private Map<String, String> mSubscribers = Collections.synchronizedMap(new ConcurrentHashMap<String, String>());

    @Override
    public synchronized <C> C get(final String name) {
        if (!containsModule(name)) {
            if (!registerModule(name)) {
                return null;
            }
        }

        try {
            if (mModules.get(name) != null) {
                return (C) mModules.get(name);
            } else {
                mModules.remove(name);
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        }
        return null;
    }

    private synchronized boolean containsModule(final String controllerName) {
        if (StringUtils.isNullOrEmpty(controllerName)) {
            return false;
        }

        return mModules.containsKey(controllerName);
    }

    @Override
    public synchronized void registerModule(final IModule controller) {
        if (controller != null && !StringUtils.isNullOrEmpty(controller.getName())) {
            final String subsciber = controller.getSubscriberType();
            if (!StringUtils.isNullOrEmpty(subsciber)) {
                mSubscribers.put(subsciber, controller.getName());
            }

            if (mModules.containsKey(controller.getName())) {
                return;
            }

            try {
                // регистрируем модуль в других модулях
                if (controller instanceof IModuleSubscriber) {
                    final List<String> types = ((IModuleSubscriber) controller).hasSubscriberType();

                    for (IModule module : mModules.values()) {
                        if (module instanceof ISmallController) {
                            if (types.contains(module.getSubscriberType())) {
                                if (!module.getName().equalsIgnoreCase(controller.getName())) {
                                    ((ISmallController) module).register(controller);
                                }
                            }
                        }
                    }
                }

                // регистрируем другие модули в модуле
                if (controller instanceof ISmallController) {
                    final String type = controller.getSubscriberType();
                    for (IModule module : mModules.values()) {
                        if (module instanceof IModuleSubscriber) {
                            if (((IModuleSubscriber) module).hasSubscriberType().contains(type)) {
                                if (!module.getName().equalsIgnoreCase(controller.getName())) {
                                    ((ISmallController) controller).register(module);
                                }
                            }
                        }
                    }
                }
                mModules.put(controller.getName(), controller);
            } catch (Exception e) {
                ErrorController.getInstance().onError(LOG_TAG, e);
            }
        }
    }

    @Override
    public boolean registerModule(String name) {
        if (!StringUtils.isNullOrEmpty(name)) {
            if (mModules.containsKey(name)) {
                return true;
            }

            try {
                final IModule module = (IModule) Class.forName(name).newInstance();
                registerModule(module);

                final String subsciber = module.getSubscriberType();
                if (!StringUtils.isNullOrEmpty(subsciber)) {
                    mSubscribers.put(subsciber, name);
                }
                return true;
            } catch (Exception e) {
                ErrorController.getInstance().onError(LOG_TAG, e);
            }
        }
        return false;
    }

    @Override
    public synchronized void unregisterModule(final String nameController) {
        if (!StringUtils.isNullOrEmpty(nameController)) {
            try {
                if (mModules.containsKey(nameController)) {
                    final IModule module = mModules.get(nameController);
                    if (module != null) {
                        if (!module.isPersistent()) {
                            if (module instanceof IController) {
                                if (((IController) module).hasSubscribers()) {
                                    return;
                                }
                            }

                            module.onUnRegister();

                            // отменяем регистрацию в других модулях
                            if (module instanceof IModuleSubscriber) {
                                final List<String> subscribers = ((IModuleSubscriber) module).hasSubscriberType();
                                for (String subscriber : subscribers) {
                                    final IModule moduleSubscriber = mModules.get(subscriber);
                                    if (moduleSubscriber != null && moduleSubscriber instanceof ISmallController) {
                                        ((ISmallController) moduleSubscriber).unregister(module);
                                    }
                                }
                            }
                            mModules.remove(nameController);
                        }
                    } else {
                        mModules.remove(nameController);
                    }
                }
            } catch (Exception e) {
                ErrorController.getInstance().onError(LOG_TAG, e);
            }
        }
    }

    @Override
    public synchronized void register(final IModuleSubscriber subscriber) {
        if (subscriber != null && !StringUtils.isNullOrEmpty(subscriber.getName())) {
            try {
                final List<String> types = subscriber.hasSubscriberType();

                // регистрируемся subscriber в модулях
                for (String subscriberType : types) {
                    if (mSubscribers.containsKey(subscriberType)) {
                        final String module = mSubscribers.get(subscriberType);
                        if (mModules.containsKey(module)) {
                            ((ISmallController) mModules.get(module)).register(subscriber);
                        } else {
                            registerModule(module);
                            if (mModules.containsKey(module)) {
                                ((ISmallController) mModules.get(module)).register(subscriber);
                            }
                        }
                    } else {
                        ErrorController.getInstance().onError(LOG_TAG, "Not found subscriber type: " + subscriberType, false);
                    }
                }
            } catch (Exception e) {
                ErrorController.getInstance().onError(LOG_TAG, e);
            }
        }
    }

    @Override
    public synchronized void unregister(final IModuleSubscriber subscriber) {
        if (subscriber != null) {
            try {
                final List<String> types = subscriber.hasSubscriberType();
                for (IModule module : mModules.values()) {
                    if (module instanceof ISmallController) {
                        final String subscriberType = module.getSubscriberType();
                        if (!StringUtils.isNullOrEmpty(subscriberType) && types.contains(subscriberType)) {
                            ((ISmallController) module).unregister(subscriber);
                        }
                    }
                }
            } catch (Exception e) {
                ErrorController.getInstance().onError(LOG_TAG, e);
            }
        }
    }

    @Override
    public synchronized void setCurrentSubscriber(final IModuleSubscriber subscriber) {
        try {
            if (subscriber != null) {
                final List<String> types = subscriber.hasSubscriberType();
                for (IModule module : mModules.values()) {
                    if (module instanceof IController) {
                        final String moduleSubscriberType = module.getSubscriberType();
                        if (!StringUtils.isNullOrEmpty(moduleSubscriberType)) {
                            if (types.contains(moduleSubscriberType)) {
                                ((IController) module).setCurrentSubscriber(subscriber);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        }
    }
}
