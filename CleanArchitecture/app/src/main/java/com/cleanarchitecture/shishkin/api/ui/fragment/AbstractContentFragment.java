package com.cleanarchitecture.shishkin.api.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.EventBusController;
import com.cleanarchitecture.shishkin.api.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarInitEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarPrepareEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarResetEvent;
import com.cleanarchitecture.shishkin.api.presenter.SwipeRefreshPresenter;
import com.cleanarchitecture.shishkin.api.ui.activity.IOnBackPressListener;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.List;

@SuppressWarnings("unused")
public abstract class AbstractContentFragment extends AbstractFragment implements
        IContentFragment,
        IOnBackPressListener, IModuleSubscriber {

    private SwipeRefreshPresenter mSwipeRefreshPresenter = new SwipeRefreshPresenter();
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public List<String> hasSubscriberType() {
        final List<String> list = super.hasSubscriberType();
        list.add(EventBusController.SUBSCRIBER_TYPE);
        return list;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout = ViewUtils.findView(view, R.id.swipeRefreshLayout);

        mSwipeRefreshPresenter.bindView(mSwipeRefreshLayout);
        registerPresenter(mSwipeRefreshPresenter);
    }

    @Override
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mSwipeRefreshLayout;
    }

    /**
     * @return true if fragment itself or its children correctly handle back press event.
     */
    @Override
    public boolean onBackPressed() {
        boolean backPressedHandled = false;

        final FragmentManager fragmentManager = getChildFragmentManager();
        final List<Fragment> children = fragmentManager.getFragments();
        if (children != null) {
            for (final Fragment child : children) {
                if (child != null && IOnBackPressListener.class.isInstance(child) && child.getUserVisibleHint()) {
                    backPressedHandled |= ((IOnBackPressListener) child).onBackPressed();
                }
            }
        }
        return backPressedHandled;
    }

    /**
     * Dispatches result of activity launch to child fragments.
     */
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        final List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (final Fragment child : fragments) {
                if (child != null) {
                    child.onActivityResult(requestCode, resultCode, intent);
                }
            }
        }
    }

    public abstract void prepareToolbar();

    public void onRequestPermissions(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }

    @Override
    public void onClick(View view) {
        if (validate()) {
            final int id = view.getId();
            switch (id) {
                case R.id.back:
                    onBackPressed();
                    break;
            }
        }
    }

    @Override
    public void refreshData() {
    }

    @Override
    public void refreshViews() {
    }

    @Override
    public void hideProgressBar() {
        super.hideProgressBar();

        ApplicationUtils.runOnUiThread(() -> {
            if (validate()) {
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public synchronized void onToolbarPrepareEvent(ToolbarPrepareEvent event) {
        prepareToolbar();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.ASYNC)
    public synchronized void onToolbarInitEvent(ToolbarInitEvent event) {
        AdminUtils.postEvent(new ToolbarResetEvent());
    }

}
