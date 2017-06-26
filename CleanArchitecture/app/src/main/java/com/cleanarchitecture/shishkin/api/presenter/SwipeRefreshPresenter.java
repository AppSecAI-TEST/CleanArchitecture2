package com.cleanarchitecture.shishkin.api.presenter;


import android.support.v4.widget.SwipeRefreshLayout;

import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.ui.fragment.AbstractContentFragment;

import java.lang.ref.WeakReference;

public class SwipeRefreshPresenter extends AbstractPresenter {
    public static final String NAME = SwipeRefreshPresenter.class.getName();

    private WeakReference<SwipeRefreshLayout> mSwipeRefreshLayout;

    public void bindView(final SwipeRefreshLayout view) {
        if (view != null) {
            mSwipeRefreshLayout = new WeakReference<>(view);
            mSwipeRefreshLayout.get().setOnRefreshListener(() -> {
                if (validate()) {
                    refreshData();
                }
            });
        }
    }

    private void refreshData() {
        final AbstractContentFragment fragment = AdminUtils.getContentFragment();
        if (fragment != null) {
            fragment.refreshData();
        }
    }

    @Override
    public void onDestroyLifecycle() {
        super.onDestroyLifecycle();

        mSwipeRefreshLayout = null;
    }

    @Override
    public boolean validate() {
        return (super.validate()
                && mSwipeRefreshLayout != null && mSwipeRefreshLayout.get() != null
        );
    }

    @Override
    public boolean isRegister() {
        return false;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
