package com.cleanarchitecture.shishkin.base.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.base.controller.ActivityController;
import com.cleanarchitecture.shishkin.base.controller.AppPreferences;
import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.controller.IEventVendor;
import com.cleanarchitecture.shishkin.base.controller.ILifecycleSubscriber;
import com.cleanarchitecture.shishkin.base.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.base.controller.LifecycleController;
import com.cleanarchitecture.shishkin.base.controller.MailController;
import com.cleanarchitecture.shishkin.base.controller.PresenterController;
import com.cleanarchitecture.shishkin.base.event.*;
import com.cleanarchitecture.shishkin.base.event.ui.DialogResultEvent;
import com.cleanarchitecture.shishkin.base.lifecycle.ILifecycle;
import com.cleanarchitecture.shishkin.base.lifecycle.IStateable;
import com.cleanarchitecture.shishkin.base.lifecycle.Lifecycle;
import com.cleanarchitecture.shishkin.base.mail.IMail;
import com.cleanarchitecture.shishkin.base.presenter.ActivityPresenter;
import com.cleanarchitecture.shishkin.base.presenter.IPresenter;
import com.cleanarchitecture.shishkin.base.ui.dialog.MaterialDialogExt;
import com.cleanarchitecture.shishkin.base.usecases.UseCasesController;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.base.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Unbinder;

@SuppressWarnings("unused")
public abstract class AbstractActivity extends AppCompatActivity
        implements IActivity, ILifecycleSubscriber, IEventVendor, IBackStack, IMailSubscriber {

    private static final String NAME = "AbstractActivity";
    private Map<String, IPresenter> mPresenters = Collections.synchronizedMap(new HashMap<String, IPresenter>());
    private List<WeakReference<IStateable>> mLifecycleList = Collections.synchronizedList(new ArrayList<WeakReference<IStateable>>());
    private int mLifecycleState = Lifecycle.STATE_CREATE;
    private ActivityPresenter mActivityPresenter = new ActivityPresenter();
    private Unbinder mUnbinder = null;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventController.getInstance().register(this);
        LifecycleController.getInstance().register(this);
        ActivityController.getInstance().register(this);
        MailController.getInstance().register(this);

        setLifecycleStatus(Lifecycle.STATE_CREATE);

        mActivityPresenter.bindView(this);
        registerPresenter(mActivityPresenter);
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
    public void onUserInteraction() {
        // EventController.getInstance().post(new OnUserIteractionEvent());
    }

    /**
     * Finds a view that was identified by the id attribute from the XML that
     * was processed in {@link #onCreate}.
     *
     * @return The casted view if found or null otherwise.
     */
    @Override
    public <V extends View> V findView(@IdRes final int id) {
        return ViewUtils.findView(this, id);
    }

    @Override
    protected void onStart() {
        super.onStart();

        setLifecycleStatus(Lifecycle.STATE_VIEW_CREATED);

        mActivityPresenter.bindView(this);

        //EventController.getInstance().post(new OnUserIteractionEvent());
    }

    @Override
    protected void onDestroy() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }

        EventController.getInstance().unregister(this);
        ActivityController.getInstance().unregister(this);
        LifecycleController.getInstance().unregister(this);
        MailController.getInstance().unregister(this);

        setLifecycleStatus(Lifecycle.STATE_DESTROY);
        mLifecycleList.clear();

        for (IPresenter presenter : mPresenters.values()) {
            PresenterController.getInstance().unregister(presenter);
        }
        mPresenters.clear();

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ActivityController.getInstance().setCurrentSubscriber(this);
        LifecycleController.getInstance().setCurrentSubscriber(this);

        setLifecycleStatus(Lifecycle.STATE_RESUME);

        readMail();
    }

    @Override
    protected void onPause() {
        super.onPause();

        setLifecycleStatus(Lifecycle.STATE_PAUSE);
    }

    /**
     * Called to process touch screen events.  You can override this to
     * intercept all touch screen events before they are dispatched to the
     * window.  Be sure to call this implementation for touch screen events
     * that should be handled normally.
     * Default implementation provides motion event to activity child fragments.
     *
     * @param ev The touch screen event.
     * @return true if this event was consumed.
     */
    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        final boolean superRes = super.dispatchTouchEvent(ev);

        boolean childRes = false;
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final List<Fragment> children = fragmentManager.getFragments();
        if (children != null) {
            for (final Fragment child : children) {
                if (child != null && child.getView() != null &&
                        DispatchTouchEventListener.class.isInstance(child)
                        && child.getUserVisibleHint()) {
                    childRes |= ((DispatchTouchEventListener) child).dispatchTouchEvent(ev);
                }
            }
        }

        return childRes || superRes;
    }

    @Override
    public abstract String getName();

    @Override
    public void clearBackStack() {
        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        UseCasesController.getInstance().setSystemDialogShown(false);

        //EventController.getInstance().post(new OnUserIteractionEvent());

        for (int i = 0; i < permissions.length; i++) {
            AppPreferences.getInstance().putInt(this, permissions[i], grantResults[i]);
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                postEvent(new OnPermisionGrantedEvent(permissions[i]));
            } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                postEvent(new OnPermisionDeniedEvent(permissions[i]));
            }
        }

        onRequestPermissions(requestCode, permissions, grantResults);
    }

    public void onRequestPermissions(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

    @Override
    public synchronized void registerLifecycleObject(final ILifecycle object) {
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
    public void postEvent(IEvent event) {
        EventController.getInstance().post(event);
    }

    @Override
    public synchronized IPresenter getPresenter(final String name) {
        if (mPresenters.containsKey(name)) {
            return mPresenters.get(name);
        }
        return null;
    }

    @Override
    public synchronized ActivityPresenter getActivityPresenter() {
        return mActivityPresenter;
    }

    @Override
    public Unbinder getUnbinder() {
        return mUnbinder;
    }

    @Override
    public int getState() {
        return mLifecycleState;
    }

    @Override
    public void setState(int state) {
    }

    @Override
    public void setUnbinder(Unbinder unbinder) {
        mUnbinder = unbinder;
    }

    @Override
    public synchronized void readMail() {
        final List<IMail> list = MailController.getInstance().getMail(this);
        for (IMail mail : list) {
            mail.read(this);
            MailController.getInstance().removeMail(mail);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinishActivityEvent(final FinishActivityEvent event) {
        if (event.getName().equals(getName())) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackpressActivityEvent(final BackpressActivityEvent event) {
        if (event.getName().equals(getName())) {
            onBackPressed();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onClearBackStackEvent(ClearBackStackEvent event) {
        clearBackStack();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onFinishApplicationEvent(FinishApplicationEvent event) {
        if (ApplicationUtils.hasLollipop()) {
            finishAndRemoveTask();
        } else if (ApplicationUtils.hasJellyBean()) {
            finishAffinity();
        } else {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDialogResultEvent(DialogResultEvent event) {

        //EventController.getInstance().post(new OnUserIteractionEvent());

        final Bundle bundle = event.getResult();
        if (bundle.getInt("id", -1) == R.id.dialog_request_permissions) {
            final String button = bundle.getString(MaterialDialogExt.BUTTON);
            if (button != null && button.equalsIgnoreCase(MaterialDialogExt.POSITIVE)) {
                final Intent intent = new Intent();
                final String packageName = getPackageName();
                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }
    }

}
