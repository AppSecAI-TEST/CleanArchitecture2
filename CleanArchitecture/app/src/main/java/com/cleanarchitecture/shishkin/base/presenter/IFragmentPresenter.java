package com.cleanarchitecture.shishkin.base.presenter;

import android.support.annotation.IdRes;
import android.view.View;

import com.cleanarchitecture.shishkin.base.controller.ISubscriber;
import com.cleanarchitecture.shishkin.base.ui.activity.IActivity;

public interface IFragmentPresenter extends ISubscriber {

    /**
     * Find view v.
     *
     * @param <V> the type parameter
     * @param id  the id
     * @return the v
     */
    <V extends View> V findView(@IdRes final int id);

    /**
     * Gets Activity subscriber.
     *
     * @return the activity subscriber
     */
    IActivity getActivitySubscriber();

    void refreshViews();

    void refreshData();

}
