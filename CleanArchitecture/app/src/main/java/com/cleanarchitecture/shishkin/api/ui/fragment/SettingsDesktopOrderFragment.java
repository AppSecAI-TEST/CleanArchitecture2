package com.cleanarchitecture.shishkin.api.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetBackNavigationEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetTitleEvent;
import com.cleanarchitecture.shishkin.api.presenter.IPresenter;
import com.cleanarchitecture.shishkin.api.presenter.SettingsDesktopOrderPresenter;
import com.cleanarchitecture.shishkin.application.presenter.FloatingActionMenuPresenter;

public class SettingsDesktopOrderFragment extends AbstractContentFragment {

    public static final String NAME = SettingsDesktopOrderFragment.class.getName();
    public static final String ORDER = "ORDER";
    public static final String ORDER_NAME = "ORDER.NAME";

    private SettingsDesktopOrderPresenter mSettingsDesktopOrderPresenter = new SettingsDesktopOrderPresenter();

    public static SettingsDesktopOrderFragment newInstance(final String name, final String order) {
        final SettingsDesktopOrderFragment fragment = new SettingsDesktopOrderFragment();
        final Bundle args = new Bundle();
        args.putString(ORDER_NAME, name);
        args.putString(ORDER, order);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(AdminUtils.getLayoutId("fragment_desktop_order_setting", R.layout.fragment_desktop_order_setting), container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerPresenter(mSettingsDesktopOrderPresenter);
        mSettingsDesktopOrderPresenter.bindView(view, getArguments());

        final IPresenter presenter = AdminUtils.getPresenter(FloatingActionMenuPresenter.NAME);
        if (presenter != null) {
            ((FloatingActionMenuPresenter) presenter).setVisible(false);
        }
    }


    @Override
    public void prepareToolbar() {
        AdminUtils.postEvent(new ToolbarSetTitleEvent(0, getString(R.string.settings_order_title)));
        AdminUtils.postEvent(new ToolbarSetBackNavigationEvent(true));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean onBackPressed() {
        mSettingsDesktopOrderPresenter.save();
        return false;
    }

}
