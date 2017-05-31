package com.cleanarchitecture.shishkin.base.controller;

import android.content.Context;
import android.support.annotation.NonNull;

import com.cleanarchitecture.shishkin.base.event.ui.HideKeyboardEvent;
import com.cleanarchitecture.shishkin.base.event.ui.HideProgressBarEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowDialogEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowEditDialogEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowKeyboardEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowListDialogEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowMessageEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowProgressBarEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowToastEvent;
import com.cleanarchitecture.shishkin.base.ui.activity.AbstractActivity;
import com.cleanarchitecture.shishkin.base.ui.activity.IActivity;
import com.cleanarchitecture.shishkin.base.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер activities
 */
@SuppressWarnings("unused")
public class ActivityController extends AbstractController implements IActivityController {

    public static final String NAME = "ActivityController";
    private Map<String, WeakReference<IActivity>> mSubscribers;
    private WeakReference<IActivity> mCurrentSubscriber;

    public ActivityController() {
        mSubscribers = Collections.synchronizedMap(new HashMap<String, WeakReference<IActivity>>());

        EventBusController.getInstance().register(this);
    }

    private synchronized void checkNullSubscriber() {
        for (Map.Entry<String, WeakReference<IActivity>> entry : mSubscribers.entrySet()) {
            if (entry.getValue().get() == null) {
                mSubscribers.remove(entry.getKey());
            }
        }
    }

    /**
     * Зарегестрировать подписчика
     *
     * @param subscriber подписчик
     */
    @Override
    public synchronized void register(@NonNull IActivity subscriber) {
        checkNullSubscriber();

        mSubscribers.put(subscriber.getName(), new WeakReference<IActivity>(subscriber));
    }

    /**
     * Отключить подписчика
     *
     * @param subscriber подписчик
     */
    @Override
    public synchronized void unregister(@NonNull IActivity subscriber) {
        final String name = subscriber.getName();

        if (mCurrentSubscriber != null && mCurrentSubscriber.get() != null) {
            if (name.equalsIgnoreCase(mCurrentSubscriber.get().getName())) {
                mCurrentSubscriber.clear();
                mCurrentSubscriber = null;
            }
        }

        if (mSubscribers.containsKey(name)) {
            mSubscribers.remove(name);
        }

        checkNullSubscriber();
    }

    /**
     * Получить подписчика
     *
     * @return подписчик
     */
    @Override
    public synchronized IActivity getSubscriber() {
        final IActivity currentSubscriber = getCurrentSubscriber();
        if (currentSubscriber != null) {
            return currentSubscriber;
        }

        for (WeakReference<IActivity> weakReference : mSubscribers.values()) {
            final IActivity subscriber = weakReference.get();
            if (subscriber != null && subscriber instanceof AbstractActivity) {
                return subscriber;
            }
        }
        return null;
    }

    /**
     * Получить текущего подписчика
     *
     * @return текущий подписчик
     */
    @Override
    public synchronized IActivity getCurrentSubscriber() {
        if (mCurrentSubscriber != null && mCurrentSubscriber.get() != null) {
            return mCurrentSubscriber.get();
        }
        return null;
    }

    /**
     * Установить текущего подписчика
     *
     * @param subscriber подписчик
     */
    @Override
    public synchronized void setCurrentSubscriber(@NonNull IActivity subscriber) {
        mCurrentSubscriber = new WeakReference<>(subscriber);
    }

    /**
     * Контроллировать права приложения
     *
     * @param permission право приложения
     * @return the boolean флаг - право приложению предоставлено
     */
    @Override
    public synchronized boolean checkPermission(String permission) {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null && subscriber.getActivityPresenter() != null) {
            return subscriber.getActivityPresenter().checkPermission(permission);
        }
        return true;
    }

    /**
     * Запросить предоставление права приложению
     *
     * @param permission  право приложения
     * @param helpMessage сообщение, выводимое в диалоге предоставления права
     */
    @Override
    public synchronized void grantPermission(String permission, String helpMessage) {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null && subscriber.getActivityPresenter() != null) {
            subscriber.getActivityPresenter().grantPermission(permission, helpMessage);
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onShowMessageEvent(ShowMessageEvent event) {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null && subscriber.getActivityPresenter() != null) {
            if (StringUtils.isNullOrEmpty(event.getAction())) {
                subscriber.getActivityPresenter().showMessage(event.getMessage());
            } else {
                subscriber.getActivityPresenter().showMessage(event.getMessage(), event.getDuration(), event.getAction());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onShowToastEvent(ShowToastEvent event) {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null && subscriber.getActivityPresenter() != null) {
            subscriber.getActivityPresenter().showToast(event.getMessage(), event.getDuration(), event.getType());
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onHideKeyboardEvent(HideKeyboardEvent event) {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null && subscriber.getActivityPresenter() != null) {
            subscriber.getActivityPresenter().hideKeyboard();
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onShowKeyboardEvent(ShowKeyboardEvent event) {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null && subscriber.getActivityPresenter() != null) {
            subscriber.getActivityPresenter().showKeyboard();
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onShowProgressBarEvent(ShowProgressBarEvent event) {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null && subscriber.getActivityPresenter() != null) {
            subscriber.getActivityPresenter().showProgressBar();
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onHideProgressBarEvent(HideProgressBarEvent event) {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null && subscriber.getActivityPresenter() != null) {
            subscriber.getActivityPresenter().hideProgressBar();
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onShowListDialogEvent(ShowListDialogEvent event) {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null && subscriber.getActivityPresenter() != null) {
            subscriber.getActivityPresenter().showListDialog(event.getId(), event.getTitle(), event.getMessage(), event.getList(), event.getSelected(), event.isMultiselect(), event.getButtonPositive(), event.getButtonNegative(), event.isCancelable());
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onShowEditDialogEvent(ShowEditDialogEvent event) {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null && subscriber.getActivityPresenter() != null) {
            subscriber.getActivityPresenter().showEditDialog(event.getId(), event.getTitle(), event.getMessage(), event.getEditText(), event.getHint(), event.getInputType(), event.getButtonPositive(), event.getButtonNegative(), event.isCancelable());
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onShowDialogEvent(ShowDialogEvent event) {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null && subscriber.getActivityPresenter() != null) {
            subscriber.getActivityPresenter().showDialog(event.getId(), event.getTitle(), event.getMessage(), event.getButtonPositive(), event.getButtonNegative(), event.isCancelable());
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
