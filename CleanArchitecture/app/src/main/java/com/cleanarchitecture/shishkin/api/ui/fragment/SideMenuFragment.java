package com.cleanarchitecture.shishkin.api.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.presenter.SideMenuPresenter;

@SuppressWarnings("unused")
public class SideMenuFragment extends AbstractFragment {

    public static final String NAME = SideMenuFragment.class.getName();

    private SideMenuPresenter mSideMenuPresenter = new SideMenuPresenter();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(AdminUtils.getLayoutId("sidemenu", R.layout.sidemenu), container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerPresenter(mSideMenuPresenter);
        mSideMenuPresenter.bindView(view);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
