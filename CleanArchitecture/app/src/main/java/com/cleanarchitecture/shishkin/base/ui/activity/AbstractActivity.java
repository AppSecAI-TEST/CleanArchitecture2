package com.cleanarchitecture.shishkin.base.ui.activity;

import android.annotation.TargetApi;
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
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.base.controller.AppPreferences;
import com.cleanarchitecture.shishkin.base.controller.Controllers;
import com.cleanarchitecture.shishkin.base.controller.EventBusController;
import com.cleanarchitecture.shishkin.base.controller.IEventVendor;
import com.cleanarchitecture.shishkin.base.controller.ILifecycleSubscriber;
import com.cleanarchitecture.shishkin.base.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.base.event.BackpressActivityEvent;
import com.cleanarchitecture.shishkin.base.event.ClearBackStackEvent;
import com.cleanarchitecture.shishkin.base.event.FinishActivityEvent;
import com.cleanarchitecture.shishkin.base.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.base.event.IEvent;
import com.cleanarchitecture.shishkin.base.event.OnPermisionDeniedEvent;
import com.cleanarchitecture.shishkin.base.event.OnPermisionGrantedEvent;
import com.cleanarchitecture.shishkin.base.event.ui.DialogResultEvent;
import com.cleanarchitecture.shishkin.base.lifecycle.ILifecycle;
import com.cleanarchitecture.shishkin.base.lifecycle.IStateable;
import com.cleanarchitecture.shishkin.base.lifecycle.Lifecycle;
import com.cleanarchitecture.shishkin.base.mail.IMail;
import com.cleanarchitecture.shishkin.base.presenter.IPresenter;
import com.cleanarchitecture.shishkin.base.ui.dialog.MaterialDialogExt;
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
    private Unbinder mUnbinder = null;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBusController.getInstance().register(this);
        Controllers.getInstance().getLifecycleController().register(this);
        Controllers.getInstance().getActivityController().register(this);
        Controllers.getInstance().getMailController().register(this);

        setLifecycleStatus(Lifecycle.STATE_CREATE);
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

        //EventController.getInstance().post(new OnUserIteractionEvent());
    }

    @Override
    protected void onDestroy() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }

        EventBusController.getInstance().unregister(this);
        Controllers.getInstance().getActivityController().unregister(this);
        Controllers.getInstance().getLifecycleController().unregister(this);
        Controllers.getInstance().getMailController().unregister(this);

        setLifecycleStatus(Lifecycle.STATE_DESTROY);
        mLifecycleList.clear();

        for (IPresenter presenter : mPresenters.values()) {
            Controllers.getInstance().getPresenterController().unregister(presenter);
        }
        mPresenters.clear();

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Controllers.getInstance().getActivityController().setCurrentSubscriber(this);
        Controllers.getInstance().getLifecycleController().setCurrentSubscriber(this);

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

        Controllers.getInstance().getUseCasesController().setSystemDialogShown(false);

        //EventController.getInstance().post(new OnUserIteractionEvent());

        for (int i = 0; i < permissions.length; i++) {
            AppPreferences.putInt(this, permissions[i], grantResults[i]);
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
            Controllers.getInstance().getPresenterController().unregister(presenter);
        }
        mPresenters.put(presenter.getName(), presenter);
        Controllers.getInstance().getPresenterController().register(presenter);
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
        EventBusController.getInstance().post(event);
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
        final List<IMail> list = Controllers.getInstance().getMailController().getMail(this);
        for (IMail mail : list) {
            mail.read(this);
            Controllers.getInstance().getMailController().removeMail(mail);
        }
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
