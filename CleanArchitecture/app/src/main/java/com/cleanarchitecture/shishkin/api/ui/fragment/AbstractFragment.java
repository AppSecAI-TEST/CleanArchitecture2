package com.cleanarchitecture.shishkin.api.ui.fragment;

import android.app.Activity;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.ActivityController;
import com.cleanarchitecture.shishkin.api.controller.Admin;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.IActivityController;
import com.cleanarchitecture.shishkin.api.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.api.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.api.controller.MailController;
import com.cleanarchitecture.shishkin.api.presenter.IPresenter;
import com.cleanarchitecture.shishkin.api.ui.activity.IActivity;
import com.cleanarchitecture.shishkin.common.lifecycle.Lifecycle;
import com.cleanarchitecture.shishkin.common.lifecycle.StateMachine;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.tooltip.Tooltip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public abstract class AbstractFragment extends LifecycleFragment implements IFragment
        , IMailSubscriber, IModuleSubscriber {

    private Map<String, IPresenter> mPresenters = Collections.synchronizedMap(new ConcurrentHashMap<String, IPresenter>());
    private StateMachine mStateMachine = new StateMachine(Lifecycle.STATE_CREATE);

    @Override
    public <V extends View> V findView(@IdRes final int id) {
        final View root = getView();
        if (root != null) {
            return ViewUtils.findView(root, id);
        }
        return null;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mStateMachine.setState(Lifecycle.STATE_READY);

        AdminUtils.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        mStateMachine.setState(Lifecycle.STATE_PAUSE);
    }

    @Override
    public void onResume() {
        super.onResume();

        mStateMachine.setState(Lifecycle.STATE_RESUME);

        AdminUtils.readMail(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mStateMachine.setState(Lifecycle.STATE_DESTROY);
        mStateMachine.clear();

        mPresenters.clear();

        AdminUtils.unregister(this);
    }

    @Override
    public abstract String getName();

    @Override
    public List<String> hasSubscriberType() {
        final ArrayList<String> list = new ArrayList<>();
        list.add(MailController.SUBSCRIBER_TYPE);
        return list;
    }

    @Override
    public LifecycleActivity getLifecycleActivity() {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            if (activity instanceof LifecycleActivity) {
                return (LifecycleActivity) activity;
            }
        } else {
            final IActivityController controller = Admin.getInstance().get(ActivityController.NAME);
            if (controller != null) {
                final IActivity subscriber = controller.getSubscriber();
                if (subscriber != null && subscriber instanceof LifecycleActivity) {
                    return (LifecycleActivity) subscriber;
                }
            }
        }
        return null;
    }

    @Override
    public synchronized IActivity getActivitySubscriber() {
        final Activity activity = getActivity();
        if (activity != null && activity instanceof IActivity) {
            return (IActivity) activity;
        }
        final IActivityController controller = Admin.getInstance().get(ActivityController.NAME);
        if (controller != null) {
            return controller.getSubscriber();
        }
        return null;
    }

    @Override
    public synchronized void registerPresenter(final IPresenter presenter) {
        AdminUtils.register(presenter);
        mPresenters.put(presenter.getName(), presenter);
        mStateMachine.addObserver(presenter);
    }

    @Override
    public synchronized IPresenter getPresenter(final String name) {
        if (mPresenters.containsKey(name)) {
            return mPresenters.get(name);
        }
        return null;
    }

    @Override
    public int getState() {
        return mStateMachine.getState();
    }

    @Override
    public void setState(int state) {
    }

    @Override
    public boolean validate() {
        return (getState() != Lifecycle.STATE_DESTROY);
    }

    @Override
    public void showProgressBar() {
        if (validate()) {
            final View view = findView(R.id.presenterProgressBar);
            if (view != null) {
                ApplicationUtils.runOnUiThread(() -> {
                    view.setVisibility(View.VISIBLE);
                });
            }
        }
    }

    @Override
    public void showCircleProgressBar(final float progress) {
        if (validate()) {
            final View view = findView(R.id.presenterCircleProgressBarLL);
            final DonutProgress progressBar = findView(R.id.presenterCircleProgressBar);
            if (progressBar != null && view != null) {
                ApplicationUtils.runOnUiThread(() -> {
                    view.setVisibility(View.VISIBLE);
                    progressBar.setProgress(progress);
                });
            }
        }
    }

    @Override
    public void hideProgressBar() {
        if (validate()) {
            final View view = findView(R.id.presenterProgressBar);
            if (view != null) {
                ApplicationUtils.runOnUiThread(() -> {
                    view.setVisibility(View.INVISIBLE);
                });
            }
        }
    }

    @Override
    public void hideCircleProgressBar() {
        if (validate()) {
            final View view = findView(R.id.presenterCircleProgressBarLL);
            if (view != null) {
                ApplicationUtils.runOnUiThread(() -> {
                    view.setVisibility(View.INVISIBLE);
                });
            }
        }
    }

    @Override
    public void setLostStateDate(boolean lostStateDate) {
        mStateMachine.setLostStateDate(lostStateDate);
    }

    @Override
    public void showTooltip(final View anchorView, final int resId, final int gravity) {
        if (anchorView == null) {
            return;
        }

        if (validate()) {
            ApplicationUtils.runOnUiThread(() -> {
                new Tooltip.Builder(anchorView)
                        .setText(getString(resId))
                        .setCancelable(true)
                        .setDismissOnClick(true)
                        .setBackgroundColor(ViewUtils.getColor(getContext(), R.color.blue))
                        .setGravity(gravity)
                        .setCornerRadius(R.dimen.dimen_4dp)
                        .setTextColor(ViewUtils.getColor(getContext(), R.color.gray_light))
                        .show();
            });
        }
    }

}
