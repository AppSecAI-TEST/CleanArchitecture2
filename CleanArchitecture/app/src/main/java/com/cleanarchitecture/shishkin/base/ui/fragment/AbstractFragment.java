package com.cleanarchitecture.shishkin.base.ui.fragment;

import android.app.Activity;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.base.controller.Controllers;
import com.cleanarchitecture.shishkin.base.controller.EventBusController;
import com.cleanarchitecture.shishkin.base.controller.IEventVendor;
import com.cleanarchitecture.shishkin.base.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.base.event.IEvent;
import com.cleanarchitecture.shishkin.base.lifecycle.Lifecycle;
import com.cleanarchitecture.shishkin.base.mail.IMail;
import com.cleanarchitecture.shishkin.base.lifecycle.StateMachine;
import com.cleanarchitecture.shishkin.base.presenter.IPresenter;
import com.cleanarchitecture.shishkin.base.ui.activity.IActivity;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.base.utils.ViewUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Unbinder;

@SuppressWarnings("unused")
public abstract class AbstractFragment extends LifecycleFragment implements IFragment
        , IEventVendor, IMailSubscriber {

    private Map<String, IPresenter> mPresenters = Collections.synchronizedMap(new HashMap<String, IPresenter>());
    private StateMachine mStateMachine = new StateMachine(Lifecycle.STATE_CREATE);
    private Unbinder mUnbinder = null;

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

        Controllers.getInstance().getMailController().register(this);
    }

    /**
     * Called when the Fragment is no longer resumed.  This is generally
     * tied to {@link Activity#onPause() Activity.onPause} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onPause() {
        super.onPause();

        mStateMachine.setState(Lifecycle.STATE_PAUSE);
    }

    @Override
    public void onResume() {
        super.onResume();

        mStateMachine.setState(Lifecycle.STATE_RESUME);

        readMail();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mStateMachine.setState(Lifecycle.STATE_DESTROY);
        mStateMachine.clear();

        for (IPresenter presenter : mPresenters.values()) {
            Controllers.getInstance().getPresenterController().unregister(presenter);
        }
        mPresenters.clear();

        Controllers.getInstance().getMailController().unregister(this);
    }

    @Override
    public void onDestroy() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }

        super.onDestroy();
    }

    @Override
    public abstract String getName();

    @Override
    public void postEvent(IEvent event) {
        EventBusController.getInstance().post(event);
    }

    @Override
    public LifecycleActivity getLifecycleActivity() {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            if (activity instanceof LifecycleActivity) {
                return (LifecycleActivity) activity;
            }
        } else {
            final IActivity subscriber = Controllers.getInstance().getActivityController().getSubscriber();
            if (subscriber != null && subscriber instanceof LifecycleActivity) {
                return (LifecycleActivity) subscriber;
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
        return Controllers.getInstance().getActivityController().getSubscriber();
    }

    @Override
    public synchronized void registerPresenter(final IPresenter presenter) {
        if (mPresenters.containsKey(presenter.getName())) {
            mPresenters.remove(presenter);
            Controllers.getInstance().getPresenterController().unregister(presenter);
        }
        mPresenters.put(presenter.getName(), presenter);
        Controllers.getInstance().getPresenterController().register(presenter);
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
    public Unbinder getUnbinder() {
        return mUnbinder;
    }

    @Override
    public void setUnbinder(Unbinder unbinder) {
        mUnbinder = unbinder;
    }

    @Override
    public synchronized void readMail() {
        List<IMail> list = Controllers.getInstance().getMailController().getMail(this);
        for (IMail mail : list) {
            mail.read(this);
            Controllers.getInstance().getMailController().removeMail(mail);
        }
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
            final View progressBar = findView(R.id.presenterProgressBar);
            if (progressBar != null) {
                ApplicationUtils.runOnUiThread(() -> {
                    progressBar.setVisibility(View.VISIBLE);
                });
            }
        }
    }

    @Override
    public void hideProgressBar() {
        if (validate()) {
            final View progressBar = findView(R.id.presenterProgressBar);
            if (progressBar != null) {
                ApplicationUtils.runOnUiThread(() -> {
                    progressBar.setVisibility(View.INVISIBLE);
                });
            }
        }
    }


}
