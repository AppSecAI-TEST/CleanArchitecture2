package com.cleanarchitecture.shishkin.base.ui.dialog;

import android.content.Context;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.controller.Controllers;
import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.event.ui.DialogResultEvent;
import com.cleanarchitecture.shishkin.base.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MaterialDialogExt {

    public static final int NO_BUTTON = -1;
    public static final int NO_TITLE = -1;
    private static final String ID = "id";
    public static final String BUTTON = "button";
    public static final String POSITIVE = "positive";
    public static final String NEGATIVE = "negative";
    private final static String NEUTRAL = "neutral";

    private int mId;
    private MaterialDialog mMaterialDialog;

    public MaterialDialogExt(final Context context, final int id,
                             final int title, final String message, final int positiveButton,
                             boolean setCancelable) {
        this(context, id, title, message, positiveButton, NO_BUTTON, NO_BUTTON, setCancelable);
    }

    public MaterialDialogExt(final Context context, final int id,
                             final int title, final String message, final int positiveButton,
                             final int negativeButton, boolean setCancelable) {
        this(context, id, title, message, positiveButton, negativeButton, NO_BUTTON, setCancelable);
    }

    public MaterialDialogExt(final Context context, final int id,
                             final int title, final String message, final int positiveButton,
                             final int negativeButton, final int neutralButton, boolean setCancelable) {

        mId = id;

        final MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        if (title != NO_TITLE) {
            builder.title(title);
        }
        if (!StringUtils.isNullOrEmpty(message)){
            builder.content(message);
        }
        builder.positiveText(positiveButton);
        if (negativeButton != NO_BUTTON) {
            builder.negativeText(negativeButton);
        }
        if (neutralButton != NO_BUTTON) {
            builder.neutralText(neutralButton);
        }
        builder.onPositive((dialog, which) -> {
            if (mId > -1){
                final Bundle bundle = new Bundle();
                bundle.putInt(ID, mId);
                bundle.putString(BUTTON, POSITIVE);
                Controllers.getInstance().getEventController().post(new DialogResultEvent(bundle));
            }
        });
        builder.onNegative((dialog, which) -> {
            if (mId > -1){
                final Bundle bundle = new Bundle();
                bundle.putInt(ID, mId);
                bundle.putString(BUTTON, NEGATIVE);
                Controllers.getInstance().getEventController().post(new DialogResultEvent(bundle));
            }
        });
        builder.onNeutral((dialog, which) -> {
            if (mId > -1){
                final Bundle bundle = new Bundle();
                bundle.putInt(ID, mId);
                bundle.putString(BUTTON, NEUTRAL);
                Controllers.getInstance().getEventController().post(new DialogResultEvent(bundle));
            }
        });
        builder.cancelable(setCancelable);

        mMaterialDialog = builder.build();
    }

    public MaterialDialogExt(final Context context, final int id,
                             final int title, final String message, final List<String> items, final Integer[] selected, final boolean multiselect, final int positiveButton,
                             final int negativeButton, boolean setCancelable) {

        mId = id;

        final MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        if (title > 0) {
            builder.title(title);
        }
        if (!StringUtils.isNullOrEmpty(message)){
            builder.content(message);
        }
        builder.items(items);
        if (multiselect) {
            builder.itemsCallbackMultiChoice(null, (dialog, which, text) -> true);
        } else {
            builder.alwaysCallSingleChoiceCallback();
            builder.itemsCallbackSingleChoice(-1, (dialog, view, which, text) -> {
                if (mId > -1) {
                    final Bundle bundle = new Bundle();
                    bundle.putInt(ID, mId);
                    bundle.putString(BUTTON, POSITIVE);
                    final ArrayList<String> list = new ArrayList();
                    list.add(text.toString());
                    bundle.putStringArrayList("list", list);
                    Controllers.getInstance().getEventController().post(new DialogResultEvent(bundle));
                }
                dialog.dismiss();
                return true;
            });
        }
        if (multiselect) {
            if (positiveButton != NO_BUTTON) {
                builder.positiveText(positiveButton);
            }
        }
        if (negativeButton != NO_BUTTON) {
            builder.negativeText(negativeButton);
        }
        if (multiselect) {
            builder.onPositive((dialog, which) -> {
                if (mId > -1) {
                    final Bundle bundle = new Bundle();
                    bundle.putInt("id", mId);
                    bundle.putString(BUTTON, POSITIVE);
                    final ArrayList<String> list = new ArrayList();
                    final ArrayList<CharSequence> itemsCharSequence = dialog.getItems();
                    final Integer[] selected1 = dialog.getSelectedIndices();
                    for (Integer i : selected1) {
                        list.add(itemsCharSequence.get(i).toString());
                    }
                    bundle.putStringArrayList("list", list);
                    Controllers.getInstance().getEventController().post(new DialogResultEvent(bundle));
                }
            });
        }
        builder.onNegative((dialog, which) -> {
            if (mId > -1){
                final Bundle bundle = new Bundle();
                bundle.putInt(ID, mId);
                bundle.putString(BUTTON, NEGATIVE);
                Controllers.getInstance().getEventController().post(new DialogResultEvent(bundle));
            }
        });
        builder.cancelable(setCancelable);

        mMaterialDialog = builder.build();

        if (selected != null) {
            mMaterialDialog.setSelectedIndices(selected);
        }
    }


    public MaterialDialogExt(final Context context, final int id,
                             final int title, final String message, final String edittext, final String hint, final int input_type, final int positiveButton,
                             final int negativeButton, boolean setCancelable) {

        mId = id;

        final MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        if (title > 0) {
            builder.title(title);
        }
        if (!StringUtils.isNullOrEmpty(message)){
            builder.content(message);
        }
        builder.positiveText(positiveButton);
        if (negativeButton != NO_BUTTON) {
            builder.negativeText(negativeButton);
        }
        builder.inputType(input_type);
        builder.input(hint, edittext, (dialog, input) -> {
        });
        builder.onPositive((dialog, which) -> {
            if (mId > -1){
                final Bundle bundle = new Bundle();
                bundle.putInt(ID, mId);
                bundle.putString(BUTTON, POSITIVE);
                bundle.putString("object", dialog.getInputEditText().getText().toString());
                Controllers.getInstance().getEventController().post(new DialogResultEvent(bundle));
            }
        });
        builder.onNegative((dialog, which) -> {
            if (mId > -1){
                final Bundle bundle = new Bundle();
                bundle.putInt(ID, mId);
                bundle.putString(BUTTON, NEGATIVE);
                Controllers.getInstance().getEventController().post(new DialogResultEvent(bundle));
            }
        });
        builder.cancelable(setCancelable);

        mMaterialDialog = builder.build();
    }

    public void show() {
        if (mMaterialDialog != null) {
            mMaterialDialog.show();
        }
    }

}
