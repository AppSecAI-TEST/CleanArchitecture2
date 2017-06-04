package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.event.OnActivityBackPressedEvent;
import com.cleanarchitecture.shishkin.base.event.ShowFragmentEvent;
import com.cleanarchitecture.shishkin.base.event.SwitchToFragmentEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Контроллер, отвечающий за навигацию в приложении
 */
public class NavigationController extends AbstractController<INavigationSubscriber> implements INavigationController {

    public static final String NAME = "NavigationController";

    public NavigationController() {
        super();

        EventBusController.getInstance().register(this);
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
