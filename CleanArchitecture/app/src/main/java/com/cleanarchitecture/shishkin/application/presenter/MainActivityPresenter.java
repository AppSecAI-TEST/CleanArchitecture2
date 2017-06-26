package com.cleanarchitecture.shishkin.application.presenter;

import android.support.annotation.NonNull;
import android.view.View;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.api.presenter.AbstractPresenter;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

public class MainActivityPresenter extends AbstractPresenter {
    public static final String NAME = MainActivityPresenter.class.getName();

    private FloatingActionMenu mFloatingActionMenu;

    public void bindView(@NonNull final View root) {
        mFloatingActionMenu = ViewUtils.findView(root, R.id.fab_menu);

        final FloatingActionButton mFloatingActionButtonExit = ViewUtils.findView(root, R.id.fab_btn_exit);
        if (mFloatingActionButtonExit != null) {
            mFloatingActionButtonExit.setOnClickListener(this::onClickFab);
        }
    }

    private void onClickFab(View view) {
        AdminUtils.postEvent(new FinishApplicationEvent());
    }

    public FloatingActionMenu getFloatingActionMenu() {
        return mFloatingActionMenu;
    }

    @Override
    public boolean isRegister() {
        return true;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
