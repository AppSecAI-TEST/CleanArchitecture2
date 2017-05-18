package com.cleanarchitecture.shishkin.application.ui.fragment;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.base.event.OnPermisionGrantedEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.OnToolbarMenuItemClickEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarSetBackNavigationEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarSetMenuEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarSetTitleEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseFinishApplicationEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseRequestPermissionEvent;
import com.cleanarchitecture.shishkin.base.presenter.OnBackPressedPresenter;
import com.cleanarchitecture.shishkin.base.ui.fragment.AbstractContentFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

@SuppressWarnings("unused")
public class ReleaseHomeFragment extends AbstractContentFragment {


    public static final String NAME = "ReleaseHomeFragment";

    public static ReleaseHomeFragment newInstance() {
        final ReleaseHomeFragment f = new ReleaseHomeFragment();
        return f;
    }

    private OnBackPressedPresenter mOnBackPressedPresenter = new OnBackPressedPresenter();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        setUnbinder(ButterKnife.bind(this, root));

        return root;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerPresenter(mOnBackPressedPresenter);

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
        postEvent(new ToolbarSetTitleEvent(0, getString(R.string.contacts)));
        postEvent(new ToolbarSetMenuEvent(R.menu.main_menu, true));
        postEvent(new ToolbarSetBackNavigationEvent(true));
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void onClickFab(View view) {
        EventController.getInstance().post(new FinishApplicationEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onToolbarMenuItemClickEvent(OnToolbarMenuItemClickEvent event) {
        final MenuItem item = event.getMenuItem();
        switch (item.getItemId()) {
            case R.id.exit:
                postEvent(new UseCaseFinishApplicationEvent());
                break;

            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onPermisionGrantedEvent(final OnPermisionGrantedEvent event) {
        if (event.getPermission().equalsIgnoreCase(Manifest.permission.READ_CONTACTS)) {
            refreshData();
        }
    }

}

