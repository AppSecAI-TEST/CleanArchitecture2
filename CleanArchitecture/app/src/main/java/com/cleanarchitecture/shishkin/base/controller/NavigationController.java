package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.event.OnActivityBackPressedEvent;
import com.cleanarchitecture.shishkin.base.event.ShowFragmentEvent;
import com.cleanarchitecture.shishkin.base.event.SwitchToFragmentEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер, отвечающий за навигацию в приложении
 */
public class NavigationController extends AbstractController implements INavigationController{

    public static final String NAME = "NavigationController";
    private Map<String, WeakReference<INavigationSubscriber>> mSubscribers = Collections.synchronizedMap(new HashMap<String, WeakReference<INavigationSubscriber>>());
    private WeakReference<INavigationSubscriber> mCurrentSubscriber;

    public NavigationController(final IEventController controller) {
        mSubscribers = Collections.synchronizedMap(new HashMap<String, WeakReference<INavigationSubscriber>>());
        controller.register(this);
    }

    private synchronized void checkNullSubscriber() {
        for (Map.Entry<String, WeakReference<INavigationSubscriber>> entry: mSubscribers.entrySet()) {
            if (entry.getValue().get() == null) {
                mSubscribers.remove(entry.getKey());
            }
        }
    }

    /**
     * Зарегистрировать подписчика
     *
     * @param subscriber подписчик
     */
    @Override
    public synchronized void register(final INavigationSubscriber subscriber) {
        if (subscriber != null){
            checkNullSubscriber();

            mSubscribers.put(subscriber.getName(), new WeakReference<>(subscriber));
        }
    }

    /**
     * Отключить подписчика
     *
     * @param subscriber подписчик
     */
    @Override
    public synchronized void unregister(final INavigationSubscriber subscriber) {
        if (subscriber != null) {
            if (mCurrentSubscriber != null && mCurrentSubscriber.get() != null) {
                if (subscriber.getName().equalsIgnoreCase(mCurrentSubscriber.get().getName())) {
                    mCurrentSubscriber.clear();
                    mCurrentSubscriber = null;
                }
            }

            if (mSubscribers.containsKey(subscriber.getName())) {
                mSubscribers.remove(subscriber.getName());
            }

            checkNullSubscriber();
        }
    }

    /**
     * Получить подписчика
     *
     * @return подписчик
     */
    @Override
    public synchronized INavigationSubscriber getSubscriber() {
        if (mCurrentSubscriber != null && mCurrentSubscriber.get() != null) {
            return mCurrentSubscriber.get();
        }

        for (WeakReference<INavigationSubscriber> weakReference : mSubscribers.values()) {
            final INavigationSubscriber subscriber = weakReference.get();
            if (subscriber != null) {
                return subscriber;
            }
        }
        return null;
    }

    /**
     * Получить фрагмент по его id.
     *
     * @param <F> тип фрагмента
     * @param cls класс фрагмента
     * @param id  the id
     * @return фрагмент
     */
    @Override
    public synchronized <F> F getFragment(Class<F> cls, int id) {
        final INavigationSubscriber subscriber = getSubscriber();
        if (subscriber != null) {
            return subscriber.getFragment(cls, id);
        }
        return null;
    }

    /**
     * Получить ContentFragment
     *
     * @param <F> тип фрагмента
     * @param cls класс фрагмента
     * @return ContentFragment
     */
    @Override
    public synchronized <F> F getContentFragment(Class<F> cls) {
        final INavigationSubscriber subscriber = getSubscriber();
        if (subscriber != null) {
            return subscriber.getContentFragment(cls);
        }
        return null;
    }

    /**
     * Установить текущего подписчика
     *
     * @param subscriber подписчик
     */
    @Override
    public synchronized void setCurrentSubscriber(final INavigationSubscriber subscriber) {
        if (subscriber != null) {
            mCurrentSubscriber = new WeakReference<>(subscriber);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowFragmentEvent(ShowFragmentEvent event) {
        final INavigationSubscriber subscriber = getSubscriber();
        if (subscriber != null) {
            subscriber.showFragment(event.getFragment(), event.isAddToBackStack(), event.isClearBackStack(), event.isAnimate(), event.isAllowingStateLoss());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onSwitchToFragmentEvent(SwitchToFragmentEvent event) {
        final INavigationSubscriber subscriber = getSubscriber();
        if (subscriber != null) {
            return subscriber.switchToFragment(event.getName());
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onActivityBackPressedEvent(OnActivityBackPressedEvent event) {
        final INavigationSubscriber subscriber = getSubscriber();
        if (subscriber != null) {
            subscriber.onActivityBackPressed();
        }
    }

}
