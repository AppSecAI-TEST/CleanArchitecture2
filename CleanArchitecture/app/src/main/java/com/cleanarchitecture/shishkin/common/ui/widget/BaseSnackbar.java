package com.cleanarchitecture.shishkin.common.ui.widget;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.ActivityController;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;

public class BaseSnackbar {
    @CheckResult
    @NonNull
    public static Snackbar make(@NonNull final View view, @StringRes final int titleRes, final int duration, final int
            type) {
        final Context context = view.getContext();
        return make(view, context.getText(titleRes), duration, type);
    }

    @CheckResult
    public static Snackbar make(@NonNull final View view, @NonNull final CharSequence title, final int duration,
                                final int type) {
        final Snackbar snackbar = Snackbar.make(view, title, duration);
        final View snackbarView = snackbar.getView();
        final TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ViewUtils.getColor(view.getContext(), R.color.white));
        if (ApplicationUtils.hasJellyBeanMR1()) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        int backgroundColor;
        switch (type) {
            case ActivityController.TOAST_TYPE_ERROR:
                snackbar.setActionTextColor(ViewUtils.getColor(view.getContext(), R.color.gray));
                backgroundColor = R.color.red;
                break;

            case ActivityController.TOAST_TYPE_WARNING:
                snackbar.setActionTextColor(ViewUtils.getColor(view.getContext(), R.color.gray));
                backgroundColor = R.color.orange;
                break;

            default:
                snackbar.setActionTextColor(ViewUtils.getColor(view.getContext(), R.color.green));
                backgroundColor = R.color.blue;
        }
        snackbarView.setBackgroundColor(ViewUtils.getColor(view.getContext(), backgroundColor));
        textView.setSingleLine(false);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        return snackbar;
    }

    private BaseSnackbar() {
    }

}
