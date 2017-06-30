package com.cleanarchitecture.shishkin.application.ui.fragment;

import android.content.res.Configuration;
import android.location.Address;
import android.location.Location;
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
import com.cleanarchitecture.shishkin.api.controller.ILocationController;
import com.cleanarchitecture.shishkin.api.controller.ILocationSubscriber;
import com.cleanarchitecture.shishkin.api.controller.LocationController;
import com.cleanarchitecture.shishkin.api.controller.NotificationService;
import com.cleanarchitecture.shishkin.api.event.toolbar.OnToolbarClickEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.OnToolbarMenuItemClickEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetBackNavigationEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetItemEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetMenuEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetTitleEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseFinishApplicationEvent;
import com.cleanarchitecture.shishkin.api.presenter.OnBackPressedPresenter;
import com.cleanarchitecture.shishkin.api.ui.fragment.AbstractContentFragment;
import com.cleanarchitecture.shishkin.api.ui.recyclerview.event.OnRecyclerViewIdleEvent;
import com.cleanarchitecture.shishkin.api.ui.recyclerview.event.OnRecyclerViewScrolledEvent;
import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.application.presenter.FloatingActionMenuPresenter;
import com.cleanarchitecture.shishkin.application.presenter.PhoneContactPresenter;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.common.utils.ShareUtil;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

@SuppressWarnings("unused")
public class HomeFragment extends AbstractContentFragment implements ILocationSubscriber {

    public static final String NAME = HomeFragment.class.getName();

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    private OnBackPressedPresenter mOnBackPressedPresenter = new OnBackPressedPresenter();
    private PhoneContactPresenter mSearchPresenter = new PhoneContactPresenter();

    @Override
    public List<String> hasSubscriberType() {
        final List<String> list = super.hasSubscriberType();
        list.add(LocationController.SUBSCRIBER_TYPE);
        return list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(AdminUtils.getLayoutId("fragment_home", R.layout.fragment_home), container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerPresenter(mOnBackPressedPresenter);

        mSearchPresenter.bindView(view);
        registerPresenter(mSearchPresenter);

        ApplicationUtils.grantPermisions(ApplicationController.PERMISIONS, AdminUtils.getActivity());

        if (!AdminUtils.isGooglePlayServices()){
            AdminUtils.checkGooglePlayServices();
        }

        /*
        if (!AdminUtils.checkPermission(Manifest.permission.READ_CONTACTS)) {
            AdminUtils.postEvent(new UseCaseRequestPermissionEvent(Manifest.permission.READ_CONTACTS));
        } else if (!AdminUtils.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            AdminUtils.postEvent(new UseCaseRequestPermissionEvent(Manifest.permission.ACCESS_FINE_LOCATION));
        } else if (!AdminUtils.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AdminUtils.postEvent(new UseCaseRequestPermissionEvent(Manifest.permission.WRITE_EXTERNAL_STORAGE));
        }
        */
    }

    @Override
    public void refreshData() {
        mSearchPresenter.refreshData();
    }

    @Override
    public boolean onBackPressed() {
        setLostStateDate(true);

        final boolean result = super.onBackPressed();
        if (!result) {
            mOnBackPressedPresenter.onClick();
        }
        return true;
    }

    @Override
    public void prepareToolbar() {
        AdminUtils.postEvent(new ToolbarSetTitleEvent(0, getString(R.string.app_name)));
        if (ViewUtils.getOrientation(getContext()) != Configuration.ORIENTATION_LANDSCAPE) {
            AdminUtils.postEvent(new ToolbarSetMenuEvent(AdminUtils.getMenuId("main_menu", R.menu.main_menu), true));
        }
        AdminUtils.postEvent(new ToolbarSetBackNavigationEvent(true));
        AdminUtils.postEvent(new ToolbarSetItemEvent(R.mipmap.ic_share_variant, true));
    }

    @Override
    public void setLocation(Location location) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Долгота: " + String.valueOf(location.getLongitude()) + " \n");
        sb.append("Широта: " + String.valueOf(location.getLatitude() + " \n\n"));

        final ILocationController controller = Admin.getInstance().get(LocationController.NAME);
        if (controller != null) {
            final List<Address> list = controller.getAddress(location, 1);
            if (!list.isEmpty()) {
                final Address address = list.get(0);
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i));
                    if (i < address.getMaxAddressLineIndex()) {
                        sb.append(", ");
                    }
                }
            }
        }

        NotificationService.replaceMessage(getContext(), sb.toString());
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onRecyclerViewIdleEvent(final OnRecyclerViewIdleEvent event) {
        final FloatingActionMenuPresenter presenter = (FloatingActionMenuPresenter) AdminUtils.getPresenter(FloatingActionMenuPresenter.NAME);
        if (presenter != null) {
            presenter.getFloatingActionMenu().setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onRecyclerViewScrolledEvent(final OnRecyclerViewScrolledEvent event) {
        final FloatingActionMenuPresenter presenter = (FloatingActionMenuPresenter) AdminUtils.getPresenter(FloatingActionMenuPresenter.NAME);
        if (presenter != null) {
            presenter.getFloatingActionMenu().setVisibility(View.INVISIBLE);
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onToolbarClickEvent(OnToolbarClickEvent event) {
        if (event.getView() != null && event.getView().getId() == R.id.item) {
            final ShareUtil.ShareData shareData = new ShareUtil.ShareData(null, getString(R.string.test_mesage));
            ShareUtil.share(shareData, AdminUtils.getActivity());
        }
    }

}

