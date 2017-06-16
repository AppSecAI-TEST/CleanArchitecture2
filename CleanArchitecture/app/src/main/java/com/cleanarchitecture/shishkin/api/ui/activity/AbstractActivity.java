package com.cleanarchitecture.shishkin.api.ui.activity;

import android.annotation.TargetApi;
import android.arch.lifecycle.LifecycleActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.ActivityController;
import com.cleanarchitecture.shishkin.api.controller.Admin;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.AppPreferences;
import com.cleanarchitecture.shishkin.api.controller.EventBusController;
import com.cleanarchitecture.shishkin.api.controller.ILifecycleSubscriber;
import com.cleanarchitecture.shishkin.api.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.api.controller.LifecycleController;
import com.cleanarchitecture.shishkin.api.controller.MailController;
import com.cleanarchitecture.shishkin.api.event.BackpressActivityEvent;
import com.cleanarchitecture.shishkin.api.event.ClearBackStackEvent;
import com.cleanarchitecture.shishkin.api.event.FinishActivityEvent;
import com.cleanarchitecture.shishkin.api.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.api.event.OnPermisionDeniedEvent;
import com.cleanarchitecture.shishkin.api.event.OnPermisionGrantedEvent;
import com.cleanarchitecture.shishkin.api.event.OnUserIteractionEvent;
import com.cleanarchitecture.shishkin.api.event.ui.DialogResultEvent;
import com.cleanarchitecture.shishkin.api.presenter.IPresenter;
import com.cleanarchitecture.shishkin.api.ui.dialog.MaterialDialogExt;
import com.cleanarchitecture.shishkin.common.lifecycle.Lifecycle;
import com.cleanarchitecture.shishkin.common.lifecycle.StateMachine;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.Unbinder;

public abstract class AbstractActivity extends LifecycleActivity
        implements IActivity, ILifecycleSubscriber, IBackStack, IMailSubscriber {

    private Map<String, IPresenter> mPresenters = Collections.synchronizedMap(new ConcurrentHashMap<String, IPresenter>());
    private StateMachine mStateMachine = new StateMachine(Lifecycle.STATE_CREATE);
    private Unbinder mUnbinder = null;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AdminUtils.register(this);

        mStateMachine.setState(Lifecycle.STATE_CREATE);
    }

    @Override
    public void onUserInteraction() {
        AdminUtils.postEvent(new OnUserIteractionEvent());
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

        mStateMachine.setState(Lifecycle.STATE_READY);

        AdminUtils.postEvent(new OnUserIteractionEvent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mUnbinder != null) {
            mUnbinder.unbind();
        }

        mStateMachine.setState(Lifecycle.STATE_DESTROY);
        mStateMachine.clear();

        mPresenters.clear();

        AdminUtils.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Admin.getInstance().setCurrentSubscriber(this);

        mStateMachine.setState(Lifecycle.STATE_RESUME);

        AdminUtils.readMail(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mStateMachine.setState(Lifecycle.STATE_PAUSE);
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
                        IDispatchTouchEventListener.class.isInstance(child)
                        && child.getUserVisibleHint()) {
                    childRes |= ((IDispatchTouchEventListener) child).dispatchTouchEvent(ev);
                }
            }
        }

        return childRes || superRes;
    }

    @Override
    public abstract String getName();

    @Override
    public List<String> hasSubscriberType() {
        ArrayList<String> list = new ArrayList<>();
        list.add(EventBusController.SUBSCRIBER_TYPE);
        list.add(ActivityController.SUBSCRIBER_TYPE);
        list.add(LifecycleController.SUBSCRIBER_TYPE);
        list.add(MailController.SUBSCRIBER_TYPE);
        return list;
    }

    @Override
    public void clearBackStack() {
        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AdminUtils.postEvent(new OnUserIteractionEvent());

        for (int i = 0; i < permissions.length; i++) {
            AppPreferences.putInt(this, permissions[i], grantResults[i]);
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                AdminUtils.postEvent(new OnPermisionGrantedEvent(permissions[i]));
            } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                AdminUtils.postEvent(new OnPermisionDeniedEvent(permissions[i]));
            }
        }

        onRequestPermissions(requestCode, permissions, grantResults);
    }

    public void onRequestPermissions(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }

    @Override
    public synchronized void registerPresenter(final IPresenter presenter) {
        if (mPresenters.containsKey(presenter.getName())) {
            mPresenters.remove(presenter);
        }
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
    public Unbinder getUnbinder() {
        return mUnbinder;
    }

    @Override
    public int getState() {
        return mStateMachine.getState();
    }

    @Override
    public void setState(int state) {
    }

    @Override
    public void setUnbinder(Unbinder unbinder) {
        mUnbinder = unbinder;
    }

    @Override
    public AbstractActivity getActivity() {
        return this;
    }

    @Override
    public void lockOrientation() {
        switch (((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation()) {
            // Portrait
            case Surface.ROTATION_0:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;

            //Landscape
            case Surface.ROTATION_90:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;

            // Reversed landscape
            case Surface.ROTATION_270:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
        }
    }

    @Override
    public void unlockOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public boolean validate() {
        return (getState() != Lifecycle.STATE_DESTROY);
    }

    /**
     * Sets the color of the status bar to {@code color}.
     * <p>
     * For this to take effect,
     * the window must be drawing the system bar backgrounds with
     * {@link android.view.WindowManager.LayoutParams#FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS} and
     * {@link android.view.WindowManager.LayoutParams#FLAG_TRANSLUCENT_STATUS} must not be set.
     * <p>
     * If {@code color} is not opaque, consider setting
     * {@link android.view.View#SYSTEM_UI_FLAG_LAYOUT_STABLE} and
     * {@link android.view.View#SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN}.
     * <p>
     * The transitionName for the view background will be "android:status:background".
     * </p>
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setStatusBarColor(final int color) {
        if (ApplicationUtils.hasLollipop()) {
            getWindow().setStatusBarColor(color);
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinishActivityEvent(final FinishActivityEvent event) {
        if (event.getName().equals(getName())) {
            finish();
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackpressActivityEvent(final BackpressActivityEvent event) {
        if (event.getName().equals(getName())) {
            onBackPressed();
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onClearBackStackEvent(ClearBackStackEvent event) {
        clearBackStack();
    }

    @Override
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

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDialogResultEvent(DialogResultEvent event) {

        final Bundle bundle = event.getResult();
        if (bundle != null && bundle.getInt("id", -1) == R.id.dialog_request_permissions) {
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
