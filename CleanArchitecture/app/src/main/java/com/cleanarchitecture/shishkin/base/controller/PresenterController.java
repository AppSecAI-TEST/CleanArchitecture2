package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.presenter.IPresenter;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер презенторов приложения
 */
@SuppressWarnings("unused")
public class PresenterController extends AbstractController
        implements IPresenterController {

    private static final String NAME = "PresenterController";
    private Map<String, WeakReference<IPresenter>> mPresenters = Collections.synchronizedMap(new HashMap<String, WeakReference<IPresenter>>());
    private static volatile PresenterController sInstance;

    public static void instantiate() {
        if (sInstance == null) {
            synchronized (PresenterController.class) {
                if (sInstance == null) {
                    sInstance = new PresenterController();
                }
            }
        }
    }

    public static synchronized PresenterController getInstance() {
        instantiate();
        return sInstance;
    }

    private PresenterController() {
        mPresenters = Collections.synchronizedMap(new HashMap<String, WeakReference<IPresenter>>());
    }

    /**
     * Зарегистрировать presenter
     *
     * @param presenter presenter
     */
    @Override
    public synchronized void register(final IPresenter presenter) {
        if (presenter != null) {
            if (presenter.isRegister()) {
                checkNullSubscriber();

                mPresenters.put(presenter.getName(), new WeakReference<IPresenter>(presenter));
            }
        }
    }

    /**
     * Отключить presenter
     *
     * @param presenter presenter
     */
    @Override
    public synchronized void unregister(final IPresenter presenter) {
        if (presenter != null) {
            if (presenter.isRegister()) {
                if (mPresenters.containsKey(presenter.getName())) {
                    mPresenters.remove(presenter.getName());
                }

                checkNullSubscriber();
            }
        }
    }

    /**
     * Получить presenter
     *
     * @param name presenter name
     * @return presenter
     */
    @Override
    public synchronized IPresenter getPresenter(final String name) {
        if (mPresenters.containsKey(name)) {
            for (Map.Entry<String, WeakReference<IPresenter>> entry : mPresenters.entrySet()) {
                if (entry.getValue().get().getName().equalsIgnoreCase(name)) {
                    return entry.getValue().get();
                }
            }
        }
        return null;
    }

    private synchronized void checkNullSubscriber() {
        for (Map.Entry<String, WeakReference<IPresenter>> entry : mPresenters.entrySet()) {
            if (entry.getValue().get() == null) {
                mPresenters.remove(entry.getKey());
            }
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

}
