package com.cleanarchitecture.shishkin.api.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetBackNavigationEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetMenuEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetTitleEvent;
import com.cleanarchitecture.shishkin.api.presenter.ApplicationSettingsPresenter;
import com.cleanarchitecture.shishkin.api.presenter.IPresenter;
import com.cleanarchitecture.shishkin.application.presenter.FloatingActionMenuPresenter;

@SuppressWarnings("unused")
public class SettingApplicationFragment extends AbstractContentFragment {

    public static final String NAME = SettingApplicationFragment.class.getName();

    public static SettingApplicationFragment newInstance() {
        final SettingApplicationFragment f = new SettingApplicationFragment();
        return f;
    }

    private ApplicationSettingsPresenter mApplicationSettingsPresenter = new ApplicationSettingsPresenter();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(AdminUtils.getLayoutId("fragment_application_setting", R.layout.fragment_application_setting), container, false);
    }

    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerPresenter(mApplicationSettingsPresenter);
        mApplicationSettingsPresenter.bindView(view);

        final IPresenter presenter = AdminUtils.getPresenter(FloatingActionMenuPresenter.NAME);
        if (presenter != null) {
            ((FloatingActionMenuPresenter) presenter).setVisible(false);
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void prepareToolbar() {
        AdminUtils.postEvent(new ToolbarSetTitleEvent(0, getString(R.string.setting)));
        AdminUtils.postEvent(new ToolbarSetMenuEvent(R.menu.main_menu, false));
        AdminUtils.postEvent(new ToolbarSetBackNavigationEvent(true));
    }

    @Override
    public String getName() {
        return NAME;
    }

}

