package com.cleanarchitecture.shishkin.base.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.controller.Controllers;
import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarInitEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarPrepareEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarResetEvent;
import com.cleanarchitecture.shishkin.base.presenter.ContentFragmentPresenter;
import com.cleanarchitecture.shishkin.base.ui.activity.OnBackPressListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

@SuppressWarnings("unused")
public abstract class AbstractContentFragment extends AbstractFragment  implements
        IContentFragment,
        OnBackPressListener {

    private ContentFragmentPresenter mContentFragmentPresenter = new ContentFragmentPresenter();

    @Override
    public void onDestroyView() {
        Controllers.getInstance().getEventController().unregister(this);

        super.onDestroyView();
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Controllers.getInstance().getEventController().register(this);

        mContentFragmentPresenter.bindView(this);
        registerPresenter(mContentFragmentPresenter);
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
                if (child != null && OnBackPressListener.class.isInstance(child) && child.getUserVisibleHint()) {
                    backPressedHandled |= ((OnBackPressListener) child).onBackPressed();
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
    public ContentFragmentPresenter getContentFragmentPresenter() {
        return mContentFragmentPresenter;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public synchronized void onToolbarPrepareEvent(ToolbarPrepareEvent event) {
        prepareToolbar();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.ASYNC)
    public synchronized void onToolbarInitEvent(ToolbarInitEvent event) {
        postEvent(new ToolbarResetEvent());
    }

}
