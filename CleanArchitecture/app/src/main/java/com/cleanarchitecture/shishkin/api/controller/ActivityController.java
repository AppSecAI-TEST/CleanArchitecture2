package com.cleanarchitecture.shishkin.api.controller;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.event.ui.HideKeyboardEvent;
import com.cleanarchitecture.shishkin.api.event.ui.HideProgressBarEvent;
import com.cleanarchitecture.shishkin.api.event.ui.OnSnackBarClickEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowDialogEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowEditDialogEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowErrorMessageEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowKeyboardEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowListDialogEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowMessageEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowProgressBarEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowToastEvent;
import com.cleanarchitecture.shishkin.api.ui.activity.AbstractContentActivity;
import com.cleanarchitecture.shishkin.api.ui.activity.IActivity;
import com.cleanarchitecture.shishkin.api.ui.dialog.MaterialDialogExt;
import com.cleanarchitecture.shishkin.api.ui.fragment.AbstractContentFragment;
import com.cleanarchitecture.shishkin.common.ui.widget.BaseSnackbar;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Контроллер activities
 */
@SuppressWarnings("unused")
public class ActivityController extends AbstractController<IActivity> implements IActivityController, IModuleSubscriber {

    public static final String NAME = ActivityController.class.getName();
    public static final String SUBSCRIBER_TYPE = IActivity.class.getName();
    private static final String LOG_TAG = "ActivityController:";

    public static final int TOAST_TYPE_INFO = 0;
    public static final int TOAST_TYPE_ERROR = 1;
    public static final int TOAST_TYPE_WARNING = 2;
    public static final int TOAST_TYPE_SUCCESS = 3;

    public ActivityController() {
        super();
    }

    @Override
    public synchronized boolean checkPermission(String permission) {
        if (ApplicationUtils.hasMarshmallow()) {
            final IActivity subscriber = getSubscriber();
            if (subscriber != null) {
                if (subscriber.validate()) {
                    if (ActivityCompat.checkSelfPermission(subscriber.getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                } else {
                    ErrorController.getInstance().onError(LOG_TAG + "checkPermission", ErrorController.ERROR_ACTIVITY_NOT_VALID, false);
                }
            } else {
                ErrorController.getInstance().onError(LOG_TAG + "checkPermission", ErrorController.ERROR_NOT_FOUND_ACTIVITY, false);
            }
        }
        return true;
    }

    @Override
    public void checkGooglePlayServices() {
        ApplicationUtils.runOnUiThread(() -> {
            final IActivity subscriber = getCurrentSubscriber();
            if (subscriber != null) {
                if (subscriber.validate()) {
                    final GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
                    final int result = googleAPI.isGooglePlayServicesAvailable(subscriber.getActivity());
                    if (result != ConnectionResult.SUCCESS) {
                        if (googleAPI.isUserResolvableError(result)) {
                            final Dialog dialog = googleAPI.getErrorDialog(subscriber.getActivity(), result, AdminUtils.REQUEST_GOOGLE_PLAY_SERVICES);
                            dialog.setOnCancelListener(dialogInterface -> subscriber.getActivity().finish());
                            dialog.show();
                        }
                    }
                } else {
                    ErrorController.getInstance().onError(LOG_TAG + "checkGooglePlayServices", ErrorController.ERROR_ACTIVITY_NOT_VALID, false);
                }
            } else {
                ErrorController.getInstance().onError(LOG_TAG + "checkGooglePlayServices", ErrorController.ERROR_NOT_FOUND_ACTIVITY, false);
            }
        });
    }

    @Override
    public synchronized void grantPermission(String permission, String helpMessage) {
        ApplicationUtils.runOnUiThread(() -> {
            if (ApplicationUtils.hasMarshmallow()) {
                final IActivity subscriber = getCurrentSubscriber();
                if (subscriber != null) {
                    if (subscriber.validate()) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(subscriber.getActivity(), permission)) {
                            AdminUtils.postEvent(new ShowDialogEvent(R.id.dialog_request_permissions, -1, helpMessage, R.string.setting, R.string.cancel, false));
                        } else {
                            ActivityCompat.requestPermissions(subscriber.getActivity(), new String[]{permission}, AdminUtils.REQUEST_PERMISSIONS);
                        }
                    } else {
                        ErrorController.getInstance().onError(LOG_TAG + "grantPermission", ErrorController.ERROR_ACTIVITY_NOT_VALID, false);
                    }
                } else {
                    ErrorController.getInstance().onError(LOG_TAG + "grantPermission", ErrorController.ERROR_NOT_FOUND_ACTIVITY, false);
                }
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return SUBSCRIBER_TYPE;
    }

    @Override
    public List<String> hasSubscriberType() {
        ArrayList<String> list = new ArrayList<>();
        list.add(EventBusController.SUBSCRIBER_TYPE);
        return list;
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(ShowMessageEvent event) {
        final IActivity subscriber = getCurrentSubscriber();
        if (subscriber != null) {
            if (subscriber.validate()) {
                final String action = event.getAction();
                if (StringUtils.isNullOrEmpty(action)) {
                    BaseSnackbar.make(subscriber.getActivity().findView(android.R.id.content), event.getMessage(), event.getDuration())
                            .show();
                } else {
                    BaseSnackbar.make(subscriber.getActivity().findView(android.R.id.content), event.getMessage(), event.getDuration())
                            .setAction(action, this::onSnackbarClick)
                            .show();
                }
            } else {
                ErrorController.getInstance().onError(LOG_TAG + "onShowMessageEvent", ErrorController.ERROR_ACTIVITY_NOT_VALID, false);
            }
        } else {
            ErrorController.getInstance().onError(LOG_TAG + "onShowMessageEvent", ErrorController.ERROR_NOT_FOUND_ACTIVITY, false);
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowErrorMessageEvent(ShowErrorMessageEvent event) {
        final IActivity subscriber = getCurrentSubscriber();
        if (subscriber != null) {
            if (subscriber.validate()) {
                new MaterialDialogExt(subscriber.getActivity(), event.getId(), R.string.error, event.getMessage(), R.string.ok_upper, MaterialDialogExt.NO_BUTTON, false).show();
            } else {
                ErrorController.getInstance().onError(LOG_TAG + "onShowErrorMessageEvent", ErrorController.ERROR_ACTIVITY_NOT_VALID, false);
            }
        } else {
            ErrorController.getInstance().onError(LOG_TAG + "onShowErrorMessageEvent", ErrorController.ERROR_NOT_FOUND_ACTIVITY, false);
        }
    }

    private void onSnackbarClick(final View view) {
        String action = null;
        if (view instanceof AppCompatButton) {
            action = ((AppCompatButton) view).getText().toString();
        } else if (view instanceof Button) {
            action = ((Button) view).getText().toString();
        }
        if (!StringUtils.isNullOrEmpty(action)) {
            AdminUtils.postEvent(new OnSnackBarClickEvent(action));
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowToastEvent(ShowToastEvent event) {
        final Context context = AdminUtils.getContext();
        if (context == null) {
            return;
        }

        final int type = event.getType();
        final int duration = event.getDuration();
        final String message = event.getMessage();

        switch (type) {
            case TOAST_TYPE_INFO:
                Toasty.info(context, message, duration).show();
                break;

            case TOAST_TYPE_ERROR:
                Toasty.error(context, message, duration).show();
                break;

            case TOAST_TYPE_WARNING:
                Toasty.warning(context, message, duration).show();
                break;

            case TOAST_TYPE_SUCCESS:
                Toasty.success(context, message, duration).show();
                break;

            default:
                Toasty.info(context, message, duration).show();
                break;

        }

        Toasty.info(AdminUtils.getContext(), event.getMessage(), event.getDuration()).show();
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHideKeyboardEvent(HideKeyboardEvent event) {
        final IActivity subscriber = getCurrentSubscriber();
        if (subscriber != null) {
            if (subscriber.validate()) {
                subscriber.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                InputMethodManager imm = (InputMethodManager) subscriber.getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    View view = subscriber.getActivity().getCurrentFocus();
                    if (view == null) {
                        view = new View(subscriber.getActivity());
                    }
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            } else {
                ErrorController.getInstance().onError(LOG_TAG + "onHideKeyboardEvent", ErrorController.ERROR_ACTIVITY_NOT_VALID, false);
            }
        } else {
            ErrorController.getInstance().onError(LOG_TAG + "onHideKeyboardEvent", ErrorController.ERROR_NOT_FOUND_ACTIVITY, false);
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowKeyboardEvent(ShowKeyboardEvent event) {
        final IActivity subscriber = getCurrentSubscriber();
        if (subscriber != null) {
            if (subscriber.validate()) {
                if (subscriber.validate()) {
                    subscriber.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            } else {
                ErrorController.getInstance().onError(LOG_TAG + "onShowKeyboardEvent", ErrorController.ERROR_ACTIVITY_NOT_VALID, false);
            }
        } else {
            ErrorController.getInstance().onError(LOG_TAG + "onShowKeyboardEvent", ErrorController.ERROR_NOT_FOUND_ACTIVITY, false);
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowProgressBarEvent(ShowProgressBarEvent event) {
        final IActivity subscriber = getCurrentSubscriber();
        if (subscriber != null) {
            if (subscriber.validate()) {
                if (subscriber instanceof AbstractContentActivity) {
                    final AbstractContentActivity activity = (AbstractContentActivity) subscriber;
                    final AbstractContentFragment fragment = activity.getContentFragment(AbstractContentFragment.class);
                    if (fragment != null) {
                        fragment.showProgressBar();
                    }
                }
            } else {
                ErrorController.getInstance().onError(LOG_TAG + "onShowProgressBarEvent", ErrorController.ERROR_ACTIVITY_NOT_VALID, false);
            }
        } else {
            ErrorController.getInstance().onError(LOG_TAG + "onShowProgressBarEvent", ErrorController.ERROR_NOT_FOUND_ACTIVITY, false);
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHideProgressBarEvent(HideProgressBarEvent event) {
        final IActivity subscriber = getCurrentSubscriber();
        if (subscriber != null) {
            if (subscriber.validate()) {
                if (subscriber instanceof AbstractContentActivity) {
                    final AbstractContentActivity activity = (AbstractContentActivity) subscriber;
                    final AbstractContentFragment fragment = activity.getContentFragment(AbstractContentFragment.class);
                    if (fragment != null) {
                        fragment.hideProgressBar();
                    }
                }
            } else {
                ErrorController.getInstance().onError(LOG_TAG + "onHideProgressBarEvent", ErrorController.ERROR_ACTIVITY_NOT_VALID, false);
            }
        } else {
            ErrorController.getInstance().onError(LOG_TAG + "onHideProgressBarEvent", ErrorController.ERROR_NOT_FOUND_ACTIVITY, false);
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowListDialogEvent(ShowListDialogEvent event) {
        final IActivity subscriber = getCurrentSubscriber();
        if (subscriber != null) {
            if (subscriber.validate()) {
                new MaterialDialogExt(subscriber.getActivity(), event.getId(),
                        event.getTitle(), event.getMessage(), event.getList(), event.getSelected(), event.isMultiselect(), event.getButtonPositive(),
                        event.getButtonNegative(), event.isCancelable()).show();
            } else {
                ErrorController.getInstance().onError(LOG_TAG + "onShowListDialogEvent", ErrorController.ERROR_ACTIVITY_NOT_VALID, false);
            }
        } else {
            ErrorController.getInstance().onError(LOG_TAG + "onShowListDialogEvent", ErrorController.ERROR_NOT_FOUND_ACTIVITY, false);
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowEditDialogEvent(ShowEditDialogEvent event) {
        final IActivity subscriber = getCurrentSubscriber();
        if (subscriber != null) {
            if (subscriber.validate()) {
                new MaterialDialogExt(subscriber.getActivity(), event.getId(), event.getTitle(), event.getMessage(), event.getEditText(), event.getHint(), event.getInputType(), event.getButtonPositive(),
                        event.getButtonNegative(), event.isCancelable()).show();
            } else {
                ErrorController.getInstance().onError(LOG_TAG + "onShowEditDialogEvent", ErrorController.ERROR_ACTIVITY_NOT_VALID, false);
            }
        } else {
            ErrorController.getInstance().onError(LOG_TAG + "onShowEditDialogEvent", ErrorController.ERROR_NOT_FOUND_ACTIVITY, false);
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowDialogEvent(ShowDialogEvent event) {
        final IActivity subscriber = getCurrentSubscriber();
        if (subscriber != null) {
            if (subscriber.validate()) {
                new MaterialDialogExt(subscriber.getActivity(), event.getId(), event.getTitle(), event.getMessage(), event.getButtonPositive(), event.getButtonNegative(), event.isCancelable()).show();
            } else {
                ErrorController.getInstance().onError(LOG_TAG + "onShowDialogEvent", ErrorController.ERROR_ACTIVITY_NOT_VALID, false);
            }
        } else {
            ErrorController.getInstance().onError(LOG_TAG + "onShowDialogEvent", ErrorController.ERROR_NOT_FOUND_ACTIVITY, false);
        }
    }
}
