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
import com.cleanarchitecture.shishkin.api.controller.EventBusController;
import com.cleanarchitecture.shishkin.api.controller.IMailSubscriber;
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
import com.cleanarchitecture.shishkin.common.state.StateObservable;
import com.cleanarchitecture.shishkin.common.state.ViewStateObserver;
import com.cleanarchitecture.shishkin.common.utils.AppPreferencesUtils;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractActivity extends LifecycleActivity
        implements IActivity, IBackStack, IMailSubscriber {

    private Map<String, IPresenter> mPresenters = Collections.synchronizedMap(new ConcurrentHashMap<String, IPresenter>());
    private StateObservable mStateObservable = new StateObservable(ViewStateObserver.STATE_CREATE);

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!AdminUtils.getPreferences().getScreenshotEnabled()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        AdminUtils.register(this);

        mStateObservable.setState(ViewStateObserver.STATE_CREATE);
    }

    @Override
    public void onUserInteraction() {
        AdminUtils.postEvent(new OnUserIteractionEvent());
    }

    @Override
    public <V extends View> V findView(@IdRes final int id) {
        return ViewUtils.findView(this, id);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mStateObservable.setState(ViewStateObserver.STATE_READY);

        AdminUtils.postEvent(new OnUserIteractionEvent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mStateObservable.setState(ViewStateObserver.STATE_DESTROY);
        mStateObservable.clear();

        mPresenters.clear();

        AdminUtils.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Admin.getInstance().setCurrentSubscriber(this);

        mStateObservable.setState(ViewStateObserver.STATE_RESUME);

        AdminUtils.readMail(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mStateObservable.setState(ViewStateObserver.STATE_PAUSE);
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
    public List<String> getSubscription() {
        return StringUtils.arrayToList(
                EventBusController.NAME,
                ActivityController.NAME,
                MailController.NAME
        );
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
            AppPreferencesUtils.putInt(this, permissions[i], grantResults[i]);
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
        AdminUtils.register(presenter);
        mPresenters.put(presenter.getName(), presenter);
        mStateObservable.addObserver(presenter);
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
        return mStateObservable.getState();
    }

    @Override
    public void setState(int state) {
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

            // Reversed portrait
            case Surface.ROTATION_180:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;

            // Reversed landscape
            case Surface.ROTATION_270:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;

            default:
                break;
        }
    }

    @Override
    public void unlockOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public boolean validate() {
        return (getState() != ViewStateObserver.STATE_DESTROY && !isFinishing());
    }

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
