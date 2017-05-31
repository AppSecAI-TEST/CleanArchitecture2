package com.cleanarchitecture.shishkin.base.controller;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.event.ui.HideKeyboardEvent;
import com.cleanarchitecture.shishkin.base.event.ui.HideProgressBarEvent;
import com.cleanarchitecture.shishkin.base.event.ui.OnSnackBarClickEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowDialogEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowEditDialogEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowKeyboardEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowListDialogEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowMessageEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowProgressBarEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowToastEvent;
import com.cleanarchitecture.shishkin.base.ui.activity.AbstractActivity;
import com.cleanarchitecture.shishkin.base.ui.activity.AbstractContentActivity;
import com.cleanarchitecture.shishkin.base.ui.activity.IActivity;
import com.cleanarchitecture.shishkin.base.ui.dialog.MaterialDialogExt;
import com.cleanarchitecture.shishkin.base.ui.fragment.AbstractContentFragment;
import com.cleanarchitecture.shishkin.base.ui.widget.BaseSnackbar;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.base.utils.StringUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static com.cleanarchitecture.shishkin.base.utils.ApplicationUtils.runOnUiThread;

/**
 * Контроллер activities
 */
@SuppressWarnings("unused")
public class ActivityController extends AbstractController implements IActivityController {

    public static final String NAME = "ActivityController";

    public static final int TOAST_TYPE_INFO = 0;
    public static final int TOAST_TYPE_ERROR = 1;
    public static final int TOAST_TYPE_WARNING = 2;
    public static final int TOAST_TYPE_SUCCESS = 3;

    private Map<String, WeakReference<IActivity>> mSubscribers;
    private WeakReference<IActivity> mCurrentSubscriber;

    public ActivityController() {
        mSubscribers = Collections.synchronizedMap(new HashMap<String, WeakReference<IActivity>>());

        EventBusController.getInstance().register(this);
    }

    private synchronized void checkNullSubscriber() {
        for (Map.Entry<String, WeakReference<IActivity>> entry : mSubscribers.entrySet()) {
            if (entry.getValue().get() == null) {
                mSubscribers.remove(entry.getKey());
            }
        }
    }

    /**
     * Зарегестрировать подписчика
     *
     * @param subscriber подписчик
     */
    @Override
    public synchronized void register(@NonNull IActivity subscriber) {
        checkNullSubscriber();

        mSubscribers.put(subscriber.getName(), new WeakReference<IActivity>(subscriber));
    }

    /**
     * Отключить подписчика
     *
     * @param subscriber подписчик
     */
    @Override
    public synchronized void unregister(@NonNull IActivity subscriber) {
        final String name = subscriber.getName();

        if (mCurrentSubscriber != null && mCurrentSubscriber.get() != null) {
            if (name.equalsIgnoreCase(mCurrentSubscriber.get().getName())) {
                mCurrentSubscriber.clear();
                mCurrentSubscriber = null;
            }
        }

        if (mSubscribers.containsKey(name)) {
            mSubscribers.remove(name);
        }

        checkNullSubscriber();
    }

    /**
     * Получить подписчика
     *
     * @return подписчик
     */
    @Override
    public synchronized IActivity getSubscriber() {
        final IActivity currentSubscriber = getCurrentSubscriber();
        if (currentSubscriber != null) {
            return currentSubscriber;
        }

        for (WeakReference<IActivity> weakReference : mSubscribers.values()) {
            final IActivity subscriber = weakReference.get();
            if (subscriber != null && subscriber instanceof AbstractActivity) {
                return subscriber;
            }
        }
        return null;
    }

    /**
     * Получить текущего подписчика
     *
     * @return текущий подписчик
     */
    @Override
    public synchronized IActivity getCurrentSubscriber() {
        if (mCurrentSubscriber != null && mCurrentSubscriber.get() != null) {
            return mCurrentSubscriber.get();
        }
        return null;
    }

    /**
     * Установить текущего подписчика
     *
     * @param subscriber подписчик
     */
    @Override
    public synchronized void setCurrentSubscriber(@NonNull IActivity subscriber) {
        mCurrentSubscriber = new WeakReference<>(subscriber);
    }

    /**
     * Контроллировать права приложения
     *
     * @param permission право приложения
     * @return the boolean флаг - право приложению предоставлено
     */
    @Override
    public synchronized boolean checkPermission(String permission) {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null) {
            if (ApplicationUtils.hasMarshmallow() && ActivityCompat.checkSelfPermission(subscriber.getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkGooglePlayServices() {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null) {
            final GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
            final int result = googleAPI.isGooglePlayServicesAvailable(subscriber.getActivity());
            if (result != ConnectionResult.SUCCESS) {
                if (googleAPI.isUserResolvableError(result)) {
                    if (!Controllers.getInstance().getUseCasesController().isSystemDialogShown()) {
                        Controllers.getInstance().getUseCasesController().setSystemDialogShown(true);
                        runOnUiThread(() -> {
                            final Dialog dialog = googleAPI.getErrorDialog(subscriber.getActivity(), result, ApplicationUtils.REQUEST_GOOGLE_PLAY_SERVICES);
                            dialog.setOnCancelListener(dialogInterface -> subscriber.getActivity().finish());
                            dialog.setOnDismissListener(dialog1 -> Controllers.getInstance().getUseCasesController().setSystemDialogShown(false));
                            dialog.show();
                        });
                    }
                }
            }
        }
        return false;
    }

    /**
     * Запросить предоставление права приложению
     *
     * @param permission  право приложения
     * @param helpMessage сообщение, выводимое в диалоге предоставления права
     */
    @Override
    public synchronized void grantPermission(String permission, String helpMessage) {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null && subscriber.validate()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(subscriber.getActivity(), permission)) {
                EventBusController.getInstance().post(new ShowDialogEvent(R.id.dialog_request_permissions, -1, helpMessage, R.string.setting, R.string.cancel, false));
            } else {
                if (!Controllers.getInstance().getUseCasesController().isSystemDialogShown()) {
                    Controllers.getInstance().getUseCasesController().setSystemDialogShown(true);
                    subscriber.getActivity().runOnUiThread(() -> {
                        ActivityCompat.requestPermissions(subscriber.getActivity(), new String[]{permission}, ApplicationUtils.REQUEST_PERMISSIONS);
                    });
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onShowMessageEvent(ShowMessageEvent event) {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null && subscriber.validate()) {
            final String action = event.getAction();
            if (StringUtils.isNullOrEmpty(action)) {
                subscriber.getActivity().runOnUiThread(() -> BaseSnackbar.make(subscriber.getActivity().findView(android.R.id.content), event.getMessage(), event.getDuration())
                        .show());
            } else {
                subscriber.getActivity().runOnUiThread(() -> BaseSnackbar.make(subscriber.getActivity().findView(android.R.id.content), event.getMessage(), event.getDuration())
                        .setAction(action, this::onSnackbarClick)
                        .show());
            }
        }
    }

    private void onSnackbarClick(final View view) {
        final String action = ((AppCompatButton) view).getText().toString();
        EventBusController.getInstance().post(new OnSnackBarClickEvent(action));
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onShowToastEvent(ShowToastEvent event) {
        final Context context = ApplicationController.getInstance();
        if (context == null) {
            return;
        }

        final int type = event.getType();
        final int duration = event.getDuration();
        final String message = event.getMessage();

        ApplicationUtils.runOnUiThread(() -> {
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

            Toasty.info(ApplicationController.getInstance(), event.getMessage(), event.getDuration()).show();
        });
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onHideKeyboardEvent(HideKeyboardEvent event) {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null && subscriber.validate()) {
            subscriber.getActivity().runOnUiThread(() -> {
                subscriber.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                final InputMethodManager imm = (InputMethodManager) subscriber.getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    View view = subscriber.getActivity().getCurrentFocus();
                    if (view == null) {
                        view = new View(subscriber.getActivity());
                    }
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onShowKeyboardEvent(ShowKeyboardEvent event) {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null && subscriber.validate()) {
            subscriber.getActivity().runOnUiThread(() -> {
                subscriber.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onShowProgressBarEvent(ShowProgressBarEvent event) {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null && subscriber.validate()) {
            if (subscriber instanceof AbstractContentActivity) {
                final AbstractContentActivity activity = (AbstractContentActivity) subscriber;
                if (activity != null) {
                    final AbstractContentFragment fragment = activity.getContentFragment(AbstractContentFragment.class);
                    if (fragment != null) {
                        fragment.showProgressBar();
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onHideProgressBarEvent(HideProgressBarEvent event) {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null && subscriber.validate()) {
            if (subscriber instanceof AbstractContentActivity) {
                final AbstractContentActivity activity = (AbstractContentActivity) subscriber;
                if (activity != null) {
                    final AbstractContentFragment fragment = activity.getContentFragment(AbstractContentFragment.class);
                    if (fragment != null) {
                        fragment.hideProgressBar();
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onShowListDialogEvent(ShowListDialogEvent event) {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null && subscriber.validate()) {
            subscriber.getActivity().runOnUiThread(() -> new MaterialDialogExt(subscriber.getActivity(), event.getId(),
                    event.getTitle(), event.getMessage(), event.getList(), event.getSelected(), event.isMultiselect(), event.getButtonPositive(),
                    event.getButtonNegative(), event.isCancelable()).show());
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onShowEditDialogEvent(ShowEditDialogEvent event) {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null && subscriber.validate()) {
            subscriber.getActivity().runOnUiThread(() -> new MaterialDialogExt(subscriber.getActivity(), event.getId(), event.getTitle(), event.getMessage(), event.getEditText(), event.getHint(), event.getInputType(), event.getButtonPositive(),
                    event.getButtonNegative(), event.isCancelable()).show());
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onShowDialogEvent(ShowDialogEvent event) {
        final IActivity subscriber = getSubscriber();
        if (subscriber != null && subscriber.validate()) {
            subscriber.getActivity().runOnUiThread(() -> new MaterialDialogExt(subscriber.getActivity(), event.getId(), event.getTitle(), event.getMessage(), event.getButtonPositive(), event.getButtonNegative(), event.isCancelable()).show());
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
