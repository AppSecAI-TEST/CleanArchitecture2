package com.cleanarchitecture.shishkin.api.controller;

import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//import com.github.snowdream.android.util.Log;

@SuppressWarnings("unused")
public abstract class AbstractAdmin implements IAdmin {
    private static final String NAME = AbstractAdmin.class.getName();
    private static final String LOG_TAG = "AbstractAdmin:";

    private Map<String, IModule> mModules = Collections.synchronizedMap(new ConcurrentHashMap<String, IModule>());

    @Override
    public synchronized <C> C get(final String controllerName) {
        if (!containsModule(controllerName)) {
            if (!registerModule(controllerName)) {
                return null;
            }
        }

        try {
            if (mModules.get(controllerName) != null) {
                return (C) mModules.get(controllerName);
            } else {
                mModules.remove(controllerName);
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
                                    //Log.i(NAME, controller.getName() + " зарегестрирован в " + module.getName());
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
                                    //Log.i(NAME, module.getName() + " зарегестрирован в " + controller.getName());
                                    ((ISmallController) controller).register(module);
                                }
                            }
                        }
                    }
                }

                //Log.i(NAME, controller.getName() + " зарегестрирован");
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
                final IModule object = (IModule) Class.forName(name).newInstance();
                registerModule(object);
                return true;
            } catch (Exception e) {
                ErrorController.getInstance().onError(LOG_TAG, e);
            }
        }
        return false;
    }

    @Override
    public synchronized void unregister(final String nameController) {
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
                                        //Log.i(NAME, module.getName() + " исключен в " + moduleSubscriber.getName());
                                        ((ISmallController) moduleSubscriber).unregister(module);
                                    }
                                }
                            }

                            //Log.i(NAME, nameController + " исключен");
                            mModules.remove(nameController);
                        }
                    } else {
                        //Log.i(NAME, nameController + " исключен");
                        mModules.remove(nameController);
                    }
                }
            } catch (Exception e) {
                ErrorController.getInstance().onError(LOG_TAG, e);
            }
        }
    }

    @Override
    public synchronized void unregisterAll() {
        for (IModule module : mModules.values()) {
            unregister(module.getName());
        }
    }

    @Override
    public synchronized void register(final IModuleSubscriber subscriber) {
        if (subscriber != null && !StringUtils.isNullOrEmpty(subscriber.getName())) {
            try {
                final List<String> types = subscriber.hasSubscriberType();

                // регистрируемся subscriber в модулях
                for (IModule module : mModules.values()) {
                    if (module instanceof ISmallController) {
                        if (types.contains(module.getSubscriberType())) {
                            if (!module.getName().equalsIgnoreCase(subscriber.getName())) {
                                //Log.i("Admin", subscriber.getName() + " зарегестрирован в " + module.getName());
                                ((ISmallController) module).register(subscriber);
                            }
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
                final List<String> types = subscriber.hasSubscriberType();
                for (IModule module : mModules.values()) {
                    if (module instanceof ISmallController) {
                        if (types.contains(module.getSubscriberType())) {
                            //Log.i(NAME, subscriber.getName() + " исключен в " + module.getName());
                            ((ISmallController) module).unregister(subscriber);
                        }
                    }
                }
                //Log.i(NAME, subscriber.getName() + " исключен");
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
