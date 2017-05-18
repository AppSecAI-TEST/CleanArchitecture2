package com.cleanarchitecture.shishkin.base.presenter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.event.ui.OnSnackBarClickEvent;
import com.cleanarchitecture.shishkin.base.lifecycle.Lifecycle;
import com.cleanarchitecture.shishkin.base.ui.activity.AbstractActivity;
import com.cleanarchitecture.shishkin.base.ui.dialog.MaterialDialogExt;
import com.cleanarchitecture.shishkin.base.ui.widget.BaseSnackbar;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.base.utils.ViewUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

import static com.cleanarchitecture.shishkin.base.utils.ApplicationUtils.runOnUiThread;

public class ActivityPresenter extends AbstractPresenter<Void> implements IActivityPresenter {
    public static final String NAME = "ActivityPresenter";

    public static final int TOAST_TYPE_INFO = 0;
    public static final int TOAST_TYPE_ERROR = 1;
    public static final int TOAST_TYPE_WARNING = 2;
    public static final int TOAST_TYPE_SUCCESS = 3;

    private WeakReference<AbstractActivity> mActivity;

    private View.OnClickListener mSnackbarClickListener = view -> onSnackbarClick(view);

    public void bindView(final AbstractActivity activity) {
        mActivity = new WeakReference<>(activity);
    }

    @Override
    public void onDestroyLifecycle() {
        mActivity = null;
    }

    private void onSnackbarClick(final View view) {
        if (validate()) {
            final String action = ((AppCompatButton) view).getText().toString();
            EventController.getInstance().post(new OnSnackBarClickEvent(action));
        }
    }

    @Override
    public boolean validate() {
        return (getState() != Lifecycle.STATE_DESTROY && mActivity != null && mActivity.get() != null);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isRegister() {
        return false;
    }

    @Override
    public <V extends View> V findView(@IdRes final int id) {
        return ViewUtils.findView(mActivity.get(), id);
    }

    @Override
    public boolean checkPermission(final String permission) {
        if (validate()) {
            if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(mActivity.get(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void showProgressBar() {
        if (validate()) {
            runOnUiThread(() -> {
                final View progressBar = findView(R.id.progressbar);
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public void hideProgressBar() {
        if (validate()) {
            runOnUiThread(() -> {
                final View progressBar = findView(R.id.progressbar);
                if (progressBar != null) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    @Override
    public void showMessage(final String message) {
        showMessage(message, Snackbar.LENGTH_LONG);
    }

    @Override
    public void showMessage(final String message, final int duration) {
        if (validate()) {
            runOnUiThread(() -> BaseSnackbar.make(findView(android.R.id.content), message, duration)
                    .show());
        }
    }

    @Override
    public void showMessage(final String message, final int duration, final String action) {
        if (validate()) {
            runOnUiThread(() -> BaseSnackbar.make(findView(android.R.id.content), message, duration)
                    .setAction(action, mSnackbarClickListener)
                    .show());
        }
    }

    @Override
    public void hideKeyboard() {
        if (validate()) {
            runOnUiThread(() -> {
                mActivity.get().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                final InputMethodManager imm = (InputMethodManager) mActivity.get().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    View view = mActivity.get().getCurrentFocus();
                    if (view == null) {
                        view = new View(mActivity.get());
                    }
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            });
        }
    }

    @Override
    public void showKeyboard() {
        if (validate()) {
            runOnUiThread(() -> {
                mActivity.get().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            });
        }
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
        if (validate()) {
            if (ApplicationUtils.hasLollipop()) {
                mActivity.get().getWindow().setStatusBarColor(color);
            }
        }
    }

    @Override
    public void lockOrientation() {
        if (validate()) {
            switch (((WindowManager) mActivity.get().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation()) {
                // Portrait
                case Surface.ROTATION_0:
                    mActivity.get().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;


                //Landscape
                case Surface.ROTATION_90:
                    mActivity.get().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;


                // Reversed landscape
                case Surface.ROTATION_270:
                    mActivity.get().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break;
            }
        }
    }

    @Override
    public void unlockOrientation() {
        if (validate()) {
            mActivity.get().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    @Override
    public void showDialog(final int id, final int title, final String message) {
        showDialog(id, title, message, R.string.ok_upper, MaterialDialogExt.NO_BUTTON, false);
    }

    @Override
    public void showDialog(final int id, final int title, final String message, final int button_positive) {
        showDialog(id, title, message, button_positive, MaterialDialogExt.NO_BUTTON, false);
    }

    @Override
    public void showDialog(final int id, final int title, final String message, final int button_positive, final int button_negative) {
        showDialog(id, title, message, button_positive, button_negative, false);
    }

    @Override
    public void showDialog(final int id, final int title, final String message, final int button_positive, final int button_negative, final boolean setCancelable) {
        if (validate()) {
            runOnUiThread(() -> new MaterialDialogExt(mActivity.get(), id, title, message, button_positive, button_negative, setCancelable).show());
        }
    }

    @Override
    public void showEditDialog(final int id, final int title, final String message, final String editText, final String hint, final int input_type, final int button_positive, final int button_negative, final boolean setCancelable) {
        if (validate()) {
            runOnUiThread(() -> new MaterialDialogExt(mActivity.get(), id, title, message, editText, hint, input_type, button_positive,
                    button_negative, setCancelable).show());
        }
    }

    @Override
    public void showListDialog(final int id, final int title, final String message, final ArrayList<String> list, final Integer[] selected, final boolean multiselect, final int button_positive, final int button_negative, final boolean setCancelable) {
        if (validate()) {
            runOnUiThread(() -> new MaterialDialogExt(mActivity.get(), id,
                    title, message, list, selected, multiselect, button_positive,
                    button_negative, setCancelable).show());
        }
    }

    @Override
    public void showToast(final String message) {
        showToast(message, Toast.LENGTH_SHORT);
    }

    @Override
    public void showToast(final String message, final int duration) {
        if (validate()) {
            runOnUiThread(() -> Toasty.info(mActivity.get(), message, duration).show());
        }
    }

    @Override
    public void showToast(final String message, final int duration, final int type) {
        if (validate()) {
            runOnUiThread(() -> {
                switch (type) {
                    case TOAST_TYPE_INFO:
                        Toasty.info(mActivity.get(), message, duration).show();
                        break;

                    case TOAST_TYPE_ERROR:
                        Toasty.error(mActivity.get(), message, duration).show();
                        break;

                    case TOAST_TYPE_WARNING:
                        Toasty.warning(mActivity.get(), message, duration).show();
                        break;

                    case TOAST_TYPE_SUCCESS:
                        Toasty.success(mActivity.get(), message, duration).show();
                        break;

                }
            });
        }
    }

}
