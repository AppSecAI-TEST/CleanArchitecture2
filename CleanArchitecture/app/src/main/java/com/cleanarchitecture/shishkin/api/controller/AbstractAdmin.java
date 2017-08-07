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

    private synchronized String getShortName(final String name) {
        return StringUtils.last(name, "\\.");
    }

    @Override
    public synchronized <C> C get(final String name) {
        if (!containsModule(name)) {
            if (!registerModule(name)) {
                return null;
            }
        }

        try {
            final String moduleName = getShortName(name);
            if (mModules.get(moduleName) != null) {
                return (C) mModules.get(moduleName);
            } else {
                mModules.remove(moduleName);
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

        return mModules.containsKey(getShortName(controllerName));
    }

    @Override
    public synchronized void registerModule(final IModule controller) {
        if (controller != null && !StringUtils.isNullOrEmpty(controller.getName())) {
            if (mModules.containsKey(getShortName(controller.getName()))) {
                return;
            }

            try {
                // регистрируем модуль в других модулях
                if (controller instanceof IModuleSubscriber) {
                    final List<String> types = ((IModuleSubscriber) controller).getSubscription();
                    for (int i = 0; i < types.size(); i++) {
                        types.set(i, getShortName(types.get(i)));
                    }

                    for (String type : types) {
                        if (mModules.containsKey(type)) {
                            ((ISmallController) mModules.get(type)).register(controller);
                        }
                    }
                }

                // регистрируем другие модули в модуле
                if (controller instanceof ISmallController) {
                    final String type = getShortName(controller.getName());
                    for (IModule module : mModules.values()) {
                        if (module instanceof IModuleSubscriber) {
                            final List<String> types = ((IModuleSubscriber) module).getSubscription();
                            for (int i = 0; i < types.size(); i++) {
                                types.set(i, getShortName(types.get(i)));
                            }

                            if (types.contains(type)) {
                                if (!getShortName(module.getName()).equalsIgnoreCase(getShortName(controller.getName()))) {
                                    ((ISmallController) controller).register(module);
                                }
                            }
                        }
                    }
                }
                mModules.put(getShortName(controller.getName()), controller);
            } catch (Exception e) {
                ErrorController.getInstance().onError(LOG_TAG, e);
            }
        }
    }

    @Override
    public boolean registerModule(String name) {
        if (!StringUtils.isNullOrEmpty(name)) {
            final String moduleName = getShortName(name);
            if (mModules.containsKey(moduleName)) {
                unregisterModule(name);
                if (mModules.containsKey(moduleName)) {
                    return true;
                }
            }

            try {
                final IModule module = (IModule) Class.forName(name).newInstance();
                registerModule(module);
                return true;
            } catch (Exception e) {
                ErrorController.getInstance().onError(LOG_TAG, e);
            }
        }
        return false;
    }

    @Override
    public synchronized void unregisterModule(final String name) {
        if (!StringUtils.isNullOrEmpty(name)) {
            try {
                final String moduleName = getShortName(name);
                if (mModules.containsKey(moduleName)) {
                    final IModule module = mModules.get(moduleName);
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
                                final List<String> subscribers = ((IModuleSubscriber) module).getSubscription();
                                for (String subscriber : subscribers) {
                                    final IModule moduleSubscriber = mModules.get(getShortName(subscriber));
                                    if (moduleSubscriber != null && moduleSubscriber instanceof ISmallController) {
                                        ((ISmallController) moduleSubscriber).unregister(module);
                                    }
                                }
                            }
                            mModules.remove(moduleName);
                        }
                    } else {
                        mModules.remove(moduleName);
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
                final List<String> types = subscriber.getSubscription();

                // регистрируемся subscriber в модулях
                for (String subscriberType : types) {
                    if (mModules.containsKey(getShortName(subscriberType))) {
                        ((ISmallController) mModules.get(getShortName(subscriberType))).register(subscriber);
                    } else {
                        registerModule(subscriberType);
                        if (mModules.containsKey(getShortName(subscriberType))) {
                            ((ISmallController) mModules.get(getShortName(subscriberType))).register(subscriber);
                        } else {
                            ErrorController.getInstance().onError(LOG_TAG, "Not found subscriber type: " + subscriberType, false);
                        }
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
                final List<String> types = subscriber.getSubscription();
                for (int i = 0; i < types.size(); i++) {
                    types.set(i, getShortName(types.get(i)));
                }

                for (IModule module : mModules.values()) {
                    if (module instanceof ISmallController) {
                        final String subscriberType = getShortName(module.getName());
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
                final List<String> types = subscriber.getSubscription();
                for (int i = 0; i < types.size(); i++) {
                    types.set(i, getShortName(types.get(i)));
                }

                for (IModule module : mModules.values()) {
                    if (module instanceof IController) {
                        final String moduleSubscriberType = getShortName(module.getName());
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
