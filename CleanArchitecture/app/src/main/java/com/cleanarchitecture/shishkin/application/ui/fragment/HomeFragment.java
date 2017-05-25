package com.cleanarchitecture.shishkin.application.ui.fragment;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.base.controller.NotificationService;
import com.cleanarchitecture.shishkin.base.controller.PresenterController;
import com.cleanarchitecture.shishkin.base.event.OnNetworkConnectedEvent;
import com.cleanarchitecture.shishkin.base.event.repository.RepositoryRequestGetImageEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.OnToolbarMenuItemClickEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarSetBackNavigationEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarSetMenuEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarSetTitleEvent;
import com.cleanarchitecture.shishkin.base.event.ui.HideHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseFinishApplicationEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseRequestPermissionEvent;
import com.cleanarchitecture.shishkin.base.net.Connectivity;
import com.cleanarchitecture.shishkin.base.presenter.FragmentPresenter;
import com.cleanarchitecture.shishkin.base.presenter.IPresenter;
import com.cleanarchitecture.shishkin.base.presenter.OnBackPressedPresenter;
import com.cleanarchitecture.shishkin.base.presenter.ToolbarPresenter;
import com.cleanarchitecture.shishkin.base.ui.fragment.AbstractContentFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings("unused")
public class HomeFragment extends AbstractContentFragment {

    public static final String NAME = "HomeFragment";

    public static HomeFragment newInstance() {
        final HomeFragment f = new HomeFragment();
        return f;
    }

    private OnBackPressedPresenter mOnBackPressedPresenter = new OnBackPressedPresenter();

    @BindView(R.id.image)
    ImageView mImage;

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

        if (Connectivity.isNetworkConnected(getContext())) {
            refreshPic();
        }

        NotificationService.addDistinctMessage(getContext(), "Тестовое сообщение");

        PresenterController.getInstance().getPresenter(ToolbarPresenter.NAME).showProgressBar();
        getFragmentPresenter().showProgressBar();
        postEvent(new ShowHorizontalProgressBarEvent());
        view.postDelayed(() -> {
            final IPresenter presenter = PresenterController.getInstance().getPresenter(ToolbarPresenter.NAME);
            if (presenter != null) {
                presenter.hideProgressBar();
            }

            final FragmentPresenter fragmentPresenter = getFragmentPresenter();
            if (fragmentPresenter != null) {
                fragmentPresenter.hideProgressBar();
            }

            postEvent(new HideHorizontalProgressBarEvent());
        }, 5000);

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
        postEvent(new ToolbarSetTitleEvent(0, getString(R.string.app_name)));
        postEvent(new ToolbarSetMenuEvent(R.menu.main_menu, true));
        postEvent(new ToolbarSetBackNavigationEvent(true));
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void refreshPic() {
        postEvent(new RepositoryRequestGetImageEvent("https://muzei-mira.com/templates/museum/images/paint/sosnovyy-les-shishkin+.jpg", mImage));
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

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onNetworkConnectedEvent(OnNetworkConnectedEvent event) {
        refreshPic();
    }


}

