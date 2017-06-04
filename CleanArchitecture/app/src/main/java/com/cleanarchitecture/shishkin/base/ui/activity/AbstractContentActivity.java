package com.cleanarchitecture.shishkin.base.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.base.controller.Controllers;
import com.cleanarchitecture.shishkin.base.controller.ErrorController;
import com.cleanarchitecture.shishkin.base.controller.INavigationSubscriber;
import com.cleanarchitecture.shishkin.base.controller.ISubscriber;
import com.cleanarchitecture.shishkin.base.event.ui.HideKeyboardEvent;
import com.cleanarchitecture.shishkin.base.ui.fragment.AbstractContentFragment;
import com.cleanarchitecture.shishkin.base.ui.fragment.AbstractFragment;
import com.cleanarchitecture.shishkin.base.ui.fragment.ToolbarFragment;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.base.utils.SafeUtils;
import com.cleanarchitecture.shishkin.base.utils.StringUtils;

import java.util.List;

public abstract class AbstractContentActivity extends AbstractActivity
        implements ActivityResultListener, INavigationSubscriber {

    private static final String NAME = "AbstractContentActivity";

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Controllers.getInstance().getNavigationController().register(this);

        addToolbar();
    }

    @Override
    protected void onDestroy() {
        Controllers.getInstance().getNavigationController().unregister(this);

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Controllers.getInstance().getNavigationController().setCurrentSubscriber(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        postEvent(new HideKeyboardEvent());
    }

    @Override
    public abstract String getName();

    @Override
    public void showFragment(final Fragment fragment) {
        showFragment(fragment, true, false, true, false);
    }

    @Override
    public void showFragment(final Fragment fragment, final boolean allowingStateLoss) {
        showFragment(fragment, true, false, true, allowingStateLoss);
    }

    @Override
    public void showFragment(final Fragment fragment, final boolean addToBackStack,
                             final boolean clearBackStack,
                             final boolean animate, final boolean allowingStateLoss) {

        runOnUiThread(() -> {
            String tag;
            if (fragment instanceof ISubscriber) {
                tag = ((ISubscriber) fragment).getName();
            } else {
                tag = fragment.getClass().getSimpleName();
            }
            final FragmentManager fm = getSupportFragmentManager();
            if (clearBackStack) {
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            final FragmentTransaction ft = fm.beginTransaction();
            if (addToBackStack) {
                ft.addToBackStack(tag);
            }
            if (animate) {
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            }
            ft.replace(R.id.content, fragment, tag);
            if (allowingStateLoss) {
                ft.commitAllowingStateLoss();
            } else {
                ft.commit();
            }
        });
    }

    @Override
    public boolean switchToFragment(@NonNull final String name) {
        if (StringUtils.isNullOrEmpty(name)) {
            return false;
        }

        try {
            final FragmentManager fm = getSupportFragmentManager();
            final List<Fragment> list = fm.getFragments();
            for (Fragment fragment : list) {
                if (fragment instanceof AbstractFragment) {
                    AbstractFragment abstractFragment = (AbstractFragment) fragment;
                    if (name.equalsIgnoreCase(abstractFragment.getName())) {
                        if (fm.popBackStackImmediate(abstractFragment.getName(), 0)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(NAME, e);
        }
        return false;
    }


    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate. Notice that you should add any {@link Fragment} that implements
     * {@link OnBackPressListener} to the back stack if you want {@link OnBackPressListener#onBackPressed()}
     * to be invoked.
     */
    @Override
    public final void onBackPressed() {
        ApplicationUtils.runOnUiThread(() -> {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            final int backStackEntryCount = fragmentManager.getBackStackEntryCount();
            if (backStackEntryCount > 0) {
                final FragmentManager.BackStackEntry backStackEntry = fragmentManager
                        .getBackStackEntryAt(backStackEntryCount - 1);
                final Fragment fragment = fragmentManager.findFragmentByTag(backStackEntry.getName());

                final OnBackPressListener onBackPressListener;
                if (OnBackPressListener.class.isInstance(fragment)) {
                    onBackPressListener = SafeUtils.cast(fragment);
                } else {
                    onBackPressListener = null;
                }

                if (onBackPressListener == null || !onBackPressListener.onBackPressed()) {
                    if (backStackEntryCount > 1) {
                        onActivityBackPressed();
                    } else {
                        supportFinishAfterTransition();
                    }
                }
            } else {
                onActivityBackPressed();
            }
        });
    }

    /**
     * Called when the activity has detected the user's press of the back
     * key. The default implementation simply finishes the current activity,
     * but you can override this to do whatever you want.
     */
    @Override
    public void onActivityBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissions(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissions(requestCode, permissions, grantResults);

        final AbstractContentFragment fragment = getContentFragment(AbstractContentFragment.class);
        if (fragment != null) {
            fragment.onRequestPermissions(requestCode, permissions, grantResults);
        }
    }

    @Nullable
    @Override
    public <F> F getContentFragment(final Class<F> cls) {
        return getFragment(cls, R.id.content);
    }

    @Nullable
    @Override
    public <F> F getFragment(final Class<F> cls, final int id) {
        F f = null;
        try {
            f = cls.cast(getSupportFragmentManager().findFragmentById(id));
        } catch (final ClassCastException e) {
            // Ignore it
        }
        return f;
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see Fragment#startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
                                 final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final AbstractContentFragment fragment = getContentFragment(AbstractContentFragment.class);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, 0, data);
        }
    }

    public synchronized void addToolbar() {
        try {
            final ToolbarFragment mToolbar = new ToolbarFragment();
            final FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().
                    replace(R.id.toolbar, mToolbar, ToolbarFragment.NAME).
                    commitAllowingStateLoss();
        } catch (Exception e) {
            ErrorController.getInstance().onError(NAME, e);
        }
    }

}