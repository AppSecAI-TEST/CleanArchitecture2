package com.cleanarchitecture.shishkin.base.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cleanarchitecture.shishkin.base.controller.ActivityController;
import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.controller.IEventVendor;
import com.cleanarchitecture.shishkin.base.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.base.controller.MailController;
import com.cleanarchitecture.shishkin.base.controller.PresenterController;
import com.cleanarchitecture.shishkin.base.event.IEvent;
import com.cleanarchitecture.shishkin.base.lifecycle.IState;
import com.cleanarchitecture.shishkin.base.lifecycle.Lifecycle;
import com.cleanarchitecture.shishkin.base.mail.IMail;
import com.cleanarchitecture.shishkin.base.presenter.ActivityPresenter;
import com.cleanarchitecture.shishkin.base.presenter.FragmentPresenter;
import com.cleanarchitecture.shishkin.base.presenter.IPresenter;
import com.cleanarchitecture.shishkin.base.ui.activity.AbstractActivity;
import com.cleanarchitecture.shishkin.base.ui.activity.IActivity;
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
    private List<WeakReference<IState>> mLifecycleList = Collections.synchronizedList(new ArrayList<WeakReference<IState>>());
    private int mLifecycleState = Lifecycle.STATE_CREATE;
    private FragmentPresenter mFragmentPresenter = new FragmentPresenter();
    private Unbinder mUnbinder = null;
    private ActivityPresenter mActivityPresenter;

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

        mLifecycleState = Lifecycle.STATE_VIEW_CREATED;

        mFragmentPresenter.bindView(this);
        registerPresenter(mFragmentPresenter);

        for (WeakReference<IState> object : mLifecycleList) {
            if (object.get() != null) {
                object.get().setState(mLifecycleState);
            }
        }

        MailController.getInstance().register(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final FragmentActivity activity = getActivity();
        if (activity != null && activity instanceof AbstractActivity) {
            mActivityPresenter = ((AbstractActivity)activity).getActivityPresenter();
        }
    }

    /**
     * Called when the Fragment is no longer resumed.  This is generally
     * tied to {@link Activity#onPause() Activity.onPause} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onPause() {
        super.onPause();

        mLifecycleState = Lifecycle.STATE_PAUSE;
        for (WeakReference<IState> object : mLifecycleList) {
            if (object.get() != null) {
                object.get().setState(mLifecycleState);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mLifecycleState = Lifecycle.STATE_RESUME;
        for (WeakReference<IState> object : mLifecycleList) {
            if (object.get() != null) {
                object.get().setState(mLifecycleState);
            }
        }

        readMail();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mLifecycleState = Lifecycle.STATE_DESTROY;
        for (WeakReference<IState> object : mLifecycleList) {
            if (object.get() != null) {
                object.get().setState(mLifecycleState);
            }
        }
        mLifecycleList.clear();

        for (IPresenter presenter: mPresenters.values()) {
            PresenterController.getInstance().unregister(presenter);
        }
        mPresenters.clear();

        MailController.getInstance().unregister(this);
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
        EventController.getInstance().post(event);
    }

    @Override
    public AppCompatActivity getAppCompatActivity() {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            if (activity instanceof AppCompatActivity) {
                return (AppCompatActivity) activity;
            }
        } else {
            final IActivity subscriber = ActivityController.getInstance().getSubscriber();
            if (subscriber != null && subscriber instanceof AppCompatActivity) {
                return (AppCompatActivity)subscriber;
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
        return ActivityController.getInstance().getSubscriber();
    }

    @Override
    public synchronized void registerPresenter(final IPresenter presenter) {
        presenter.setState(mLifecycleState);
        if (mPresenters.containsKey(presenter.getName())) {
            mPresenters.remove(presenter);
            PresenterController.getInstance().unregister(presenter);
        }
        mPresenters.put(presenter.getName(), presenter);
        PresenterController.getInstance().register(presenter);
        registerLifecycleObject(presenter);
    }

    public synchronized void registerLifecycleObject(final IState object) {
        for (WeakReference<IState> reference: mLifecycleList) {
            if (reference.get() == null) {
                mLifecycleList.remove(reference);
            }
        }

        boolean found = false;
        for (WeakReference<IState> reference: mLifecycleList) {
            if (reference.get() != null && reference.get() == object) {
                found = true;
                break;
            }
        }
        if (!found) {
            mLifecycleList.add(new WeakReference<IState>(object));
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
    public FragmentPresenter getFragmentPresenter() {
        return mFragmentPresenter;
    }

    @Override
    public ActivityPresenter getActivityPresenter() {
        if (mActivityPresenter != null) {
            return mActivityPresenter;
        } else {
            final IActivity subscriber = ActivityController.getInstance().getSubscriber();
            if (subscriber != null && subscriber instanceof AbstractActivity) {
                return subscriber.getActivityPresenter();
            }
        }
        return null;
    }

    @Override
    public void refreshData() {
    }

    @Override
    public void refreshViews() {
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
        List<IMail> list = MailController.getInstance().getMail(this);
        for (IMail mail : list) {
            mail.read(this);
            MailController.getInstance().removeMail(mail);
        }
    }

}
