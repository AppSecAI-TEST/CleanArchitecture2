package com.cleanarchitecture.shishkin.base.ui.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.cleanarchitecture.shishkin.R;

public class BaseSnackbar {
    @CheckResult
    @NonNull
    public static Snackbar make(@NonNull final View view, @StringRes final int titleRes, final int duration) {
        final Context context = view.getContext();
        return make(view, context.getText(titleRes), duration);
    }

    @CheckResult
    public static Snackbar make(@NonNull final View view, @NonNull final CharSequence title, final int duration) {
        final Snackbar snackbar = Snackbar.make(view, title, duration);
        final View snackbarView = snackbar.getView();
        final TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        textView.setSingleLine(false);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        return snackbar;
    }

    private BaseSnackbar() {
    }

}
