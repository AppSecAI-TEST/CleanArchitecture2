package com.cleanarchitecture.shishkin.base.presenter;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.base.ui.fragment.AbstractContentFragment;

import java.lang.ref.WeakReference;

public class ContentFragmentPresenter extends AbstractPresenter<Void> implements IContentFragmentPresenter {
    public static final String NAME = "ContentFragmentPresenter";

    private WeakReference<SwipeRefreshLayout> mSwipeRefreshLayout;
    private WeakReference<AbstractContentFragment> mContentFragment;

    public void bindView(@NonNull final AbstractContentFragment fragment) {
        mContentFragment = new WeakReference<>(fragment);
    }

    @Override
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.get() != null) {
            return mSwipeRefreshLayout.get();
        }
        return null;
    }

    @Override
    public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        if (swipeRefreshLayout != null) {
            mSwipeRefreshLayout = new WeakReference<>(swipeRefreshLayout);
            mSwipeRefreshLayout.get().setOnRefreshListener(() -> {
                if (validate()) {
                    mContentFragment.get().refreshData();
                }
            });
        }
    }

    @Override
    public void onDestroyLifecycle() {
        mSwipeRefreshLayout = null;
        mContentFragment = null;
    }

    @Override
    public void onClick(View view) {
        if (validate()) {
            final int id = view.getId();
            switch (id) {
                case R.id.back:
                    mContentFragment.get().onBackPressed();
                    break;
            }
        }
    }

    @Override
    public boolean validate() {
        return (super.validate()
                && mContentFragment != null && mContentFragment.get() != null);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isRegister() {
        return false;
    }
}
