package com.cleanarchitecture.shishkin.api.event.ui;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.event.AbstractEvent;
import com.cleanarchitecture.shishkin.api.ui.dialog.MaterialDialogExt;

/**
 * Событие - выполнить команду "показать Edit диалог"
 */
public class ShowEditDialogEvent extends AbstractEvent {

    private int mId;
    private int mTitle;
    private String mMessage;
    private String mEditText;
    private String mHint;
    private int mInputType;
    private int mButtonPositive = R.string.ok_upper;
    private int mButtonNegative = MaterialDialogExt.NO_BUTTON;
    private boolean mCancelable = false;

    public ShowEditDialogEvent(final int id, final int title, final String message, final String editText, final String hint, final int input_type, final int button_positive, final int button_negative, final boolean cancelable) {
        mId = id;
        mTitle = title;
        mMessage = message;
        mEditText = editText;
        mHint = hint;
        mInputType = input_type;
        mButtonPositive = button_positive;
        mButtonNegative = button_negative;
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

    public String getEditText() {
        return mEditText;
    }

    public String getHint() {
        return mHint;
    }

    public int getInputType() {
        return mInputType;
    }

    public boolean isCancelable() {
        return mCancelable;
    }

}
