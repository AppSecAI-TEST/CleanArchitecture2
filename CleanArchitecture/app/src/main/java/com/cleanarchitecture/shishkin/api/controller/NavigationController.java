package com.cleanarchitecture.shishkin.api.controller;

import com.cleanarchitecture.shishkin.api.event.OnActivityBackPressedEvent;
import com.cleanarchitecture.shishkin.api.event.ShowFragmentEvent;
import com.cleanarchitecture.shishkin.api.event.SwitchToFragmentEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер, отвечающий за навигацию в приложении
 */
public class NavigationController extends AbstractController<INavigationSubscriber>
        implements INavigationController, IModuleSubscriber {

    public static final String NAME = "NavigationController";
    public static final String SUBSCRIBER_TYPE = "INavigationSubscriber";

    public NavigationController() {
        super();
    }

    @Override
    public synchronized <F> F getFragment(Class<F> cls, int id) {
        final INavigationSubscriber subscriber = getSubscriber();
        if (subscriber != null) {
            return subscriber.getFragment(cls, id);
        }
        return null;
    }

    @Override
    public synchronized <F> F getContentFragment(Class<F> cls) {
        final INavigationSubscriber subscriber = getSubscriber();
        if (subscriber != null) {
            return subscriber.getContentFragment(cls);
        }
        return null;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return SUBSCRIBER_TYPE;
    }

    @Override
    public List<String> hasSubscriberType() {
        final ArrayList<String> list = new ArrayList<>();
        list.add(EventBusController.SUBSCRIBER_TYPE);
        return list;
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowFragmentEvent(ShowFragmentEvent event) {
        final INavigationSubscriber subscriber = getSubscriber();
        if (subscriber != null) {
            subscriber.showFragment(event.getFragment(), event.isAddToBackStack(), event.isClearBackStack(), event.isAnimate(), event.isAllowingStateLoss());
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSwitchToFragmentEvent(SwitchToFragmentEvent event) {
        final INavigationSubscriber subscriber = getSubscriber();
        if (subscriber != null) {
            subscriber.switchToFragment(event.getName());
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onActivityBackPressedEvent(OnActivityBackPressedEvent event) {
        final INavigationSubscriber subscriber = getSubscriber();
        if (subscriber != null) {
            subscriber.onActivityBackPressed();
        }
    }

}
