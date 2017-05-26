package com.cleanarchitecture.shishkin.application.ui.fragment;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.presenter.HomeFragmentPresenter;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarSetBackNavigationEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarSetMenuEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarSetTitleEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseRequestPermissionEvent;
import com.cleanarchitecture.shishkin.base.presenter.OnBackPressedPresenter;
import com.cleanarchitecture.shishkin.base.ui.fragment.AbstractContentFragment;

@SuppressWarnings("unused")
public class HomeFragment extends AbstractContentFragment {

    public static final String NAME = "HomeFragment";

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    private OnBackPressedPresenter mOnBackPressedPresenter = new OnBackPressedPresenter();
    private HomeFragmentPresenter mHomeFragmentPresenter = new HomeFragmentPresenter();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerPresenter(mOnBackPressedPresenter);

        mHomeFragmentPresenter.bindView(view, this);
        registerPresenter(mHomeFragmentPresenter);

        postEvent(new UseCaseRequestPermissionEvent(Manifest.permission.WRITE_EXTERNAL_STORAGE));

    }

    @Override
    public boolean onBackPressed() {
        final boolean result = super.onBackPressed();
        if (!result) {
            mOnBackPressedPresenter.onClick();
        }
        return true;
    }

    @Override
    public void prepareToolbar() {
        postEvent(new ToolbarSetTitleEvent(0, getString(R.string.app_name)));
        postEvent(new ToolbarSetMenuEvent(R.menu.main_menu, true));
        postEvent(new ToolbarSetBackNavigationEvent(true));
    }

    @Override
    public String getName() {
        return NAME;
    }


}

