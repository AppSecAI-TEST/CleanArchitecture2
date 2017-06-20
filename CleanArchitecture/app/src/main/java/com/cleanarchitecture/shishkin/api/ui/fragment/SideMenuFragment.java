package com.cleanarchitecture.shishkin.api.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.presenter.SideMenuPresenter;
import com.cleanarchitecture.shishkin.api.presenter.ToolbarPresenter;

@SuppressWarnings("unused")
public class SideMenuFragment extends AbstractFragment {

    public static final String NAME = SideMenuFragment.class.getName();

    private SideMenuPresenter mSideMenuPresenter = new SideMenuPresenter();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sidemenu, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSideMenuPresenter.bindView(view, getContext());
        registerPresenter(mSideMenuPresenter);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
