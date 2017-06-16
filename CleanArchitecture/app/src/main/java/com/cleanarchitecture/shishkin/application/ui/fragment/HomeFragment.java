package com.cleanarchitecture.shishkin.application.ui.fragment;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.Admin;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.DesktopController;
import com.cleanarchitecture.shishkin.api.controller.IDesktopController;
import com.cleanarchitecture.shishkin.api.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.OnToolbarMenuItemClickEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetBackNavigationEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetMenuEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetTitleEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseFinishApplicationEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseRequestPermissionEvent;
import com.cleanarchitecture.shishkin.api.presenter.OnBackPressedPresenter;
import com.cleanarchitecture.shishkin.api.ui.fragment.AbstractContentFragment;
import com.cleanarchitecture.shishkin.api.ui.recyclerview.event.OnRecyclerViewIdleEvent;
import com.cleanarchitecture.shishkin.api.ui.recyclerview.event.OnRecyclerViewScrolledEvent;
import com.cleanarchitecture.shishkin.application.presenter.SearchPresenter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings("unused")
public class HomeFragment extends AbstractContentFragment {

    public static final String NAME = HomeFragment.class.getName();

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

        final View root = inflater.inflate(AdminUtils.getLayoutId("fragment_home", R.layout.fragment_home), container, false);
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

        if (!AdminUtils.checkPermission(Manifest.permission.READ_CONTACTS)) {
            AdminUtils.postEvent(new UseCaseRequestPermissionEvent(Manifest.permission.READ_CONTACTS));
        } else if (!AdminUtils.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            AdminUtils.postEvent(new UseCaseRequestPermissionEvent(Manifest.permission.ACCESS_FINE_LOCATION));
        } else if (!AdminUtils.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AdminUtils.postEvent(new UseCaseRequestPermissionEvent(Manifest.permission.WRITE_EXTERNAL_STORAGE));
        }
    }

    private void onClickFab(View view) {
        AdminUtils.postEvent(new FinishApplicationEvent());
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
        AdminUtils.postEvent(new ToolbarSetTitleEvent(0, getString(R.string.app_name)));
        AdminUtils.postEvent(new ToolbarSetMenuEvent(R.menu.main_menu, true));
        AdminUtils.postEvent(new ToolbarSetBackNavigationEvent(true));
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
        if (item.getItemId() == R.id.exit) {
            AdminUtils.postEvent(new UseCaseFinishApplicationEvent());
        } else if (item.getItemId() == R.id.desktop) {
            final IDesktopController controller = Admin.getInstance().get(DesktopController.NAME);
            if (controller != null) {
                controller.getDesktop();
            }
        }
    }

}

