package com.cleanarchitecture.shishkin.api.event.ui;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.event.AbstractEvent;
import com.cleanarchitecture.shishkin.api.ui.dialog.MaterialDialogExt;

/**
 * Событие - выполнить команду "показать диалог"
 */
public class ShowDialogEvent extends AbstractEvent {

    private int mId;
    private int mTitle;
    private String mMessage;
    private int mButtonPositive = R.string.ok_upper;
    private int mButtonNegative = MaterialDialogExt.NO_BUTTON;
    private boolean mCancelable = false;

    public ShowDialogEvent(final int id, final int title, final String message) {
        mId = id;
        mTitle = title;
        mMessage = message;
    }

    public ShowDialogEvent(final int id, final int title, final String message, final int button_positive) {
        this(id, title, message);

        mButtonPositive = button_positive;
    }

    public ShowDialogEvent(final int id, final int title, final String message, final int button_positive, final int button_negative) {
        this(id, title, message, button_positive);

        mButtonNegative = button_negative;
    }

    public ShowDialogEvent(final int id, final int title, final String message, final int button_positive, final int button_negative, final boolean cancelable) {
        this(id, title, message, button_positive, button_negative);

        mCancelable = cancelable;
    }

    public String getMessage() {
        return mMessage;
    }

    public int getId() {
        return mId;
    }

    public int getTitle() {
        return mTitle;
    }

    public int getButtonPositive() {
        return mButtonPositive;
    }

    public int getButtonNegative() {
        return mButtonNegative;
    }

    public boolean isCancelable() {
        return mCancelable;
    }


}
