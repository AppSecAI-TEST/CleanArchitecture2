package com.cleanarchitecture.shishkin.api.controller;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.event.OnBackPressedEvent;
import com.cleanarchitecture.shishkin.api.event.ShowFragmentEvent;
import com.cleanarchitecture.shishkin.api.event.StartActivityEvent;
import com.cleanarchitecture.shishkin.api.event.SwitchToFragmentEvent;
import com.cleanarchitecture.shishkin.api.ui.activity.AbstractActivity;
import com.cleanarchitecture.shishkin.api.ui.activity.AbstractContentActivity;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Контроллер, отвечающий за навигацию в приложении
 */
public class NavigationController extends AbstractController<INavigationSubscriber>
        implements INavigationController, IModuleSubscriber {

    public static final String NAME = NavigationController.class.getName();

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
    public List<String> getSubscription() {
        return StringUtils.arrayToList(EventBusController.NAME);
    }

    @Override
    public synchronized AbstractContentActivity getContentActivity() {
        final INavigationSubscriber subscriber = getSubscriber();
        if (subscriber != null && subscriber instanceof AbstractContentActivity) {
            return (AbstractContentActivity) subscriber;
        }
        return null;
    }

    @Override
    public synchronized AbstractContentActivity getContentActivity(final String name) {
        if (!StringUtils.isNullOrEmpty(name)) {
            for (WeakReference<INavigationSubscriber> ref : getSubscribers().values()) {
                if (ref.get().getName().equalsIgnoreCase(name)) {
                    if (ref.get() instanceof AbstractContentActivity) {
                        return (AbstractContentActivity) ref.get();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public synchronized AbstractActivity getActivity() {
        final INavigationSubscriber subscriber = getSubscriber();
        if (subscriber != null && subscriber instanceof AbstractActivity) {
            return (AbstractActivity) subscriber;
        }
        return null;
    }

    @Override
    public synchronized AbstractActivity getActivity(final String name) {
        if (!StringUtils.isNullOrEmpty(name)) {
            for (WeakReference<INavigationSubscriber> ref : getSubscribers().values()) {
                if (ref.get().getName().equalsIgnoreCase(name)) {
                    if (ref.get() instanceof AbstractActivity) {
                        return (AbstractActivity) ref.get();
                    }
                }
            }
        }
        return null;
    }

    private synchronized void startActivity(final StartActivityEvent event) {
        if (event != null) {
            final Context context = AdminUtils.getContext();
            if (context != null) {
                context.startActivity(event.getIntent());
            }
        }
    }

    @Override
    public String getDescription() {
        final Context context = ApplicationController.getInstance();
        if (context != null) {
            return context.getString(R.string.module_navigation);
        }
        return "Navigation controller";
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowFragmentEvent(ShowFragmentEvent event) {
        final INavigationSubscriber subscriber = getCurrentSubscriber();
        if (subscriber != null) {
            subscriber.showFragment(event.getFragment(), event.isAddToBackStack(), event.isClearBackStack(), event.isAnimate(), event.isAllowingStateLoss());
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSwitchToFragmentEvent(SwitchToFragmentEvent event) {
        final INavigationSubscriber subscriber = getCurrentSubscriber();
        if (subscriber != null) {
            subscriber.switchToFragment(event.getName());
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onActivityBackPressedEvent(OnBackPressedEvent event) {
        final INavigationSubscriber subscriber = getCurrentSubscriber();
        if (subscriber != null) {
            subscriber.onBackPressed();
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onStartActivityEvent(final StartActivityEvent event) {
        startActivity(event);
    }

}
