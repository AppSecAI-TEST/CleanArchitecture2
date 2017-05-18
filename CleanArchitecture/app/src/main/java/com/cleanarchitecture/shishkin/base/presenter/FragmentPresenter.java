package com.cleanarchitecture.shishkin.base.presenter;

import android.app.Activity;
import android.view.View;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.base.controller.ActivityController;
import com.cleanarchitecture.shishkin.base.ui.activity.IActivity;
import com.cleanarchitecture.shishkin.base.ui.fragment.AbstractFragment;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.base.utils.ViewUtils;

import java.lang.ref.WeakReference;

public class FragmentPresenter extends AbstractPresenter<Void> implements IFragmentPresenter {
    public static final String NAME = "FragmentPresenter";

    private WeakReference<AbstractFragment> mFragment;

    public void bindView(final AbstractFragment fragment) {
        if (fragment != null) {
            mFragment = new WeakReference<>(fragment);
        }
    }

    @Override
    public IActivity getActivitySubscriber() {
        if (validate()) {
            final Activity activity = mFragment.get().getActivity();
            if (activity != null && activity instanceof IActivity) {
                return (IActivity) activity;
            }
            return ActivityController.getInstance().getSubscriber();
        }
        return null;
    }

    @Override
    public <V extends View> V findView(final int id) {
        if (validate()) {
            final View root = mFragment.get().getView();
            if (root != null) {
                return ViewUtils.findView(root, id);
            }
        }
        return null;
    }

    @Override
    public void onDestroyLifecycle() {
        mFragment = null;
    }

    @Override
    public void refreshViews() {
    }

    @Override
    public void refreshData() {
    }

    @Override
    public void showProgressBar() {
        if (validate()) {
            final View progressBar = mFragment.get().findView(R.id.progressbar);
            if (progressBar != null) {
                ApplicationUtils.runOnUiThread(() -> {
                    progressBar.setVisibility(View.VISIBLE);
                });
            } else {
                final IActivity subscriber = getActivitySubscriber();
                if (subscriber != null && subscriber.getActivityPresenter() != null) {
                    subscriber.getActivityPresenter().showProgressBar();
                }
            }
        }
    }

    @Override
    public void hideProgressBar() {
        if (validate()) {
            final View progressBar = mFragment.get().findView(R.id.progressbar);
            if (progressBar != null) {
                ApplicationUtils.runOnUiThread(() -> {
                    progressBar.setVisibility(View.INVISIBLE);
                });
            } else {
                final IActivity subscriber = getActivitySubscriber();
                if (subscriber != null && subscriber.getActivityPresenter() != null) {
                    subscriber.getActivityPresenter().hideProgressBar();
                }
            }
        }
    }

    @Override
    public boolean validate() {
        return (super.validate()
                && mFragment != null && mFragment.get() != null
        );
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isRegister() {
        return false;
    }
}
