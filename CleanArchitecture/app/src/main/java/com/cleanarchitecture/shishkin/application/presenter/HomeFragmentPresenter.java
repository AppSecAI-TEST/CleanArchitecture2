package com.cleanarchitecture.shishkin.application.presenter;

import android.view.MenuItem;
import android.view.View;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.ui.fragment.HomeFragment;
import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.controller.NotificationService;
import com.cleanarchitecture.shishkin.base.controller.PresenterController;
import com.cleanarchitecture.shishkin.base.event.toolbar.OnToolbarMenuItemClickEvent;
import com.cleanarchitecture.shishkin.base.event.ui.HideHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseFinishApplicationEvent;
import com.cleanarchitecture.shishkin.base.presenter.AbstractPresenter;
import com.cleanarchitecture.shishkin.base.presenter.FragmentPresenter;
import com.cleanarchitecture.shishkin.base.presenter.IPresenter;
import com.cleanarchitecture.shishkin.base.presenter.ToolbarPresenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

@SuppressWarnings("unused")
public class HomeFragmentPresenter extends AbstractPresenter {
    private static final String NAME = "HomeFragmentPresenter";

    private WeakReference<HomeFragment> mFragment;
    private WeakReference<View> mRoot;

    public void bindView(final View root, final HomeFragment fragment) {
        mFragment = new WeakReference<>(fragment);
        mRoot = new WeakReference<>(root);
    }

    @Override
    public void onViewCreatedLifecycle() {
        super.onViewCreatedLifecycle();

        EventController.getInstance().register(this);

        if (validate()) {
            NotificationService.addDistinctMessage(mFragment.get().getContext(), "Тестовое сообщение");

            PresenterController.getInstance().getPresenter(ToolbarPresenter.NAME).showProgressBar();
            mFragment.get().getFragmentPresenter().showProgressBar();
            postEvent(new ShowHorizontalProgressBarEvent());
            mRoot.get().postDelayed(() -> {
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
    }

    @Override
    public boolean validate() {
        return (super.validate()
                && mFragment != null && mFragment.get() != null
                && mRoot != null && mRoot.get() != null
        );
    }

    @Override
    public void onDestroyLifecycle() {
        mRoot = null;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onToolbarMenuItemClickEvent(OnToolbarMenuItemClickEvent event) {
        final MenuItem item = event.getMenuItem();
        if (item != null && item.getItemId() == R.id.exit) {
            postEvent(new UseCaseFinishApplicationEvent());
        }
    }

}
