package com.cleanarchitecture.shishkin.base.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.base.controller.Controllers;
import com.cleanarchitecture.shishkin.base.controller.EventBusController;
import com.cleanarchitecture.shishkin.base.controller.IEventVendor;
import com.cleanarchitecture.shishkin.base.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.base.event.IEvent;
import com.cleanarchitecture.shishkin.base.lifecycle.IStateable;
import com.cleanarchitecture.shishkin.base.lifecycle.Lifecycle;
import com.cleanarchitecture.shishkin.base.mail.IMail;
import com.cleanarchitecture.shishkin.base.presenter.IPresenter;
import com.cleanarchitecture.shishkin.base.ui.activity.IActivity;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.base.utils.ViewUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Unbinder;

@SuppressWarnings("unused")
public abstract class AbstractFragment extends Fragment implements IFragment
        , IEventVendor, IMailSubscriber {

    private Map<String, IPresenter> mPresenters = Collections.synchronizedMap(new HashMap<String, IPresenter>());
    private List<WeakReference<IStateable>> mLifecycleList = Collections.synchronizedList(new ArrayList<WeakReference<IStateable>>());
    private int mLifecycleState = Lifecycle.STATE_CREATE;
    private Unbinder mUnbinder = null;

    @Override
    public <V extends View> V findView(@IdRes final int id) {
        final View root = getView();
        if (root != null) {
            return ViewUtils.findView(root, id);
        }
        return null;
    }

    private void setLifecycleStatus(final int status) {
        mLifecycleState = status;
        for (WeakReference<IStateable> object : mLifecycleList) {
            if (object.get() != null) {
                object.get().setState(mLifecycleState);
            }
        }
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setLifecycleStatus(Lifecycle.STATE_VIEW_CREATED);

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

        setLifecycleStatus(Lifecycle.STATE_PAUSE);
    }

    @Override
    public void onResume() {
        super.onResume();

        setLifecycleStatus(Lifecycle.STATE_RESUME);

        readMail();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        setLifecycleStatus(Lifecycle.STATE_DESTROY);
        mLifecycleList.clear();

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
    public AppCompatActivity getAppCompatActivity() {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            if (activity instanceof AppCompatActivity) {
                return (AppCompatActivity) activity;
            }
        } else {
            final IActivity subscriber = Controllers.getInstance().getActivityController().getSubscriber();
            if (subscriber != null && subscriber instanceof AppCompatActivity) {
                return (AppCompatActivity) subscriber;
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
        presenter.setState(mLifecycleState);
        if (mPresenters.containsKey(presenter.getName())) {
            mPresenters.remove(presenter);
            Controllers.getInstance().getPresenterController().unregister(presenter);
        }
        mPresenters.put(presenter.getName(), presenter);
        Controllers.getInstance().getPresenterController().register(presenter);
        registerLifecycleObject(presenter);
    }

    public synchronized void registerLifecycleObject(final IStateable object) {
        for (WeakReference<IStateable> reference : mLifecycleList) {
            if (reference.get() == null) {
                mLifecycleList.remove(reference);
            }
        }

        boolean found = false;
        for (WeakReference<IStateable> reference : mLifecycleList) {
            if (reference.get() != null && reference.get() == object) {
                found = true;
                break;
            }
        }
        if (!found) {
            mLifecycleList.add(new WeakReference<IStateable>(object));
        }
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
        return mLifecycleState;
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
