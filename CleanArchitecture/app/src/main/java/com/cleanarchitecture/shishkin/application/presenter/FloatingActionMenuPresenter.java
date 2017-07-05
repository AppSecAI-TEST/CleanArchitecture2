package com.cleanarchitecture.shishkin.application.presenter;

import android.support.annotation.NonNull;
import android.view.View;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseFinishApplicationEvent;
import com.cleanarchitecture.shishkin.api.presenter.AbstractPresenter;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

public class FloatingActionMenuPresenter extends AbstractPresenter {
    public static final String NAME = FloatingActionMenuPresenter.class.getName();

    private FloatingActionMenu mFloatingActionMenu;

    public void bindView(@NonNull final View root) {
        mFloatingActionMenu = ViewUtils.findView(root, R.id.fab_menu);

        final FloatingActionButton mFloatingActionButtonExit = ViewUtils.findView(root, R.id.fab_btn_exit);
        if (mFloatingActionButtonExit != null) {
            mFloatingActionButtonExit.setOnClickListener(this::onClickFab);
        }
    }

    private void onClickFab(View view) {
        AdminUtils.postEvent(new UseCaseFinishApplicationEvent());
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

    @Override
    public boolean validate() {
        return (super.validate()
                && mFloatingActionMenu != null
        );
    }

    public void setVisible(boolean isVisible) {
        if (validate()) {
            if (isVisible) {
                mFloatingActionMenu.setVisibility(View.VISIBLE);
            } else {
                mFloatingActionMenu.setVisibility(View.INVISIBLE);
            }
        }
    }
}
