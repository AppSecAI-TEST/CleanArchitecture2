package com.cleanarchitecture.shishkin.application.ui.fragment;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.presenter.SearchPresenter;
import com.cleanarchitecture.shishkin.base.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.OnToolbarMenuItemClickEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarSetBackNavigationEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarSetMenuEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarSetTitleEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseFinishApplicationEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseRequestPermissionEvent;
import com.cleanarchitecture.shishkin.base.presenter.OnBackPressedPresenter;
import com.cleanarchitecture.shishkin.base.ui.fragment.AbstractContentFragment;
import com.cleanarchitecture.shishkin.base.ui.recyclerview.event.OnRecyclerViewIdleEvent;
import com.cleanarchitecture.shishkin.base.ui.recyclerview.event.OnRecyclerViewScrolledEvent;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings("unused")
public class HomeFragment extends AbstractContentFragment {

    public static final String NAME = "HomeFragment";

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    private OnBackPressedPresenter mOnBackPressedPresenter = new OnBackPressedPresenter();
    private SearchPresenter mSearchPresenter = new SearchPresenter();

    @BindView(R.id.fab_menu)
    FloatingActionMenu mFloatingActionMenu;

    @BindView(R.id.fab_btn_exit)
    FloatingActionButton mFloatingActionButtonExit;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        setUnbinder(ButterKnife.bind(this, root));

        return root;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFloatingActionButtonExit.setOnClickListener(this::onClickFab);

        registerPresenter(mOnBackPressedPresenter);

        mSearchPresenter.bindView(view, this);
        registerPresenter(mSearchPresenter);

        if (!ApplicationUtils.checkPermission(Manifest.permission.READ_CONTACTS)) {
            ApplicationUtils.postEvent(new UseCaseRequestPermissionEvent(Manifest.permission.READ_CONTACTS));
        } else if (!ApplicationUtils.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ApplicationUtils.postEvent(new UseCaseRequestPermissionEvent(Manifest.permission.WRITE_EXTERNAL_STORAGE));
        }
    }

    private void onClickFab(View view) {
        ApplicationUtils.postEvent(new FinishApplicationEvent());
    }

    @Override
    public void refreshData() {
        mSearchPresenter.refreshData();
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
        ApplicationUtils.postEvent(new ToolbarSetTitleEvent(0, getString(R.string.app_name)));
        ApplicationUtils.postEvent(new ToolbarSetMenuEvent(R.menu.main_menu, true));
        ApplicationUtils.postEvent(new ToolbarSetBackNavigationEvent(true));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onRecyclerViewIdleEvent(final OnRecyclerViewIdleEvent event) {
        mFloatingActionMenu.setVisibility(View.VISIBLE);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onRecyclerViewScrolledEvent(final OnRecyclerViewScrolledEvent event) {
        mFloatingActionMenu.setVisibility(View.INVISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onToolbarMenuItemClickEvent(OnToolbarMenuItemClickEvent event) {
        final MenuItem item = event.getMenuItem();
        if (item != null && item.getItemId() == R.id.exit) {
            ApplicationUtils.postEvent(new UseCaseFinishApplicationEvent());
        }
    }

}

