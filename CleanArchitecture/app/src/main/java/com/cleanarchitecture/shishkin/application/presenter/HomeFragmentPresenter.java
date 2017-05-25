package com.cleanarchitecture.shishkin.application.presenter;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.ui.fragment.HomeFragment;
import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.controller.NotificationService;
import com.cleanarchitecture.shishkin.base.controller.PresenterController;
import com.cleanarchitecture.shishkin.base.event.OnNetworkConnectedEvent;
import com.cleanarchitecture.shishkin.base.event.repository.RepositoryRequestGetImageEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.OnToolbarMenuItemClickEvent;
import com.cleanarchitecture.shishkin.base.event.ui.HideHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseFinishApplicationEvent;
import com.cleanarchitecture.shishkin.base.net.Connectivity;
import com.cleanarchitecture.shishkin.base.presenter.AbstractPresenter;
import com.cleanarchitecture.shishkin.base.presenter.FragmentPresenter;
import com.cleanarchitecture.shishkin.base.presenter.IPresenter;
import com.cleanarchitecture.shishkin.base.presenter.ToolbarPresenter;
import com.cleanarchitecture.shishkin.base.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

public class HomeFragmentPresenter extends AbstractPresenter {
    private static final String NAME = "HomeFragmentPresenter";

    private ImageView mImage;
    private WeakReference<HomeFragment> mFragment;
    private View mRoot;

    public void bindView(final View root, final HomeFragment fragment) {
        mImage = ViewUtils.findView(root, R.id.image);
        mFragment = new WeakReference<>(fragment);
        mRoot = root;
    }

    private void refreshPic() {
        postEvent(new RepositoryRequestGetImageEvent("https://muzei-mira.com/templates/museum/images/paint/sosnovyy-les-shishkin+.jpg", mImage));
    }

    @Override
    public void onViewCreatedLifecycle() {
        super.onViewCreatedLifecycle();

        EventController.getInstance().register(this);

        if (Connectivity.isNetworkConnected(mFragment.get().getContext())) {
            refreshPic();
        }

        NotificationService.addDistinctMessage(mFragment.get().getContext(), "Тестовое сообщение");

        PresenterController.getInstance().getPresenter(ToolbarPresenter.NAME).showProgressBar();
        mFragment.get().getFragmentPresenter().showProgressBar();
        postEvent(new ShowHorizontalProgressBarEvent());
        mRoot.postDelayed(() -> {
            if (validate()) {
                final IPresenter presenter = PresenterController.getInstance().getPresenter(ToolbarPresenter.NAME);
                if (presenter != null) {
                    presenter.hideProgressBar();
                }

                final FragmentPresenter fragmentPresenter = mFragment.get().getFragmentPresenter();
                if (fragmentPresenter != null) {
                    fragmentPresenter.hideProgressBar();
                }

                postEvent(new HideHorizontalProgressBarEvent());
            }
        }, 5000);

    }

    @Override
    public boolean validate() {
        return (super.validate()
                && mFragment != null && mFragment.get() != null
                && mImage != null
                && mRoot != null
        );
    }

    @Override
    public void onDestroyLifecycle() {
        mRoot = null;
        mImage = null;
        mFragment = null;

        EventController.getInstance().unregister(this);

        super.onDestroyLifecycle();
    }

    @Override
    public boolean isRegister() {
        return false;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onNetworkConnectedEvent(OnNetworkConnectedEvent event) {
        refreshPic();
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

}