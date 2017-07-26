package com.cleanarchitecture.shishkin.api.event.ui;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.event.AbstractEvent;
import com.cleanarchitecture.shishkin.api.ui.dialog.MaterialDialogExt;

import java.util.ArrayList;

/**
 * Событие - выполнить команду "показать List диалог"
 */
public class ShowListDialogEvent extends AbstractEvent {

    private int mId;
    private String mTitle;
    private String mMessage;
    private ArrayList<String> mList;
    private Integer[] mSelected;
    private int mButtonPositive = R.string.ok_upper;
    private int mButtonNegative = MaterialDialogExt.NO_BUTTON;
    private boolean mMultiselect = false;
    private boolean mSetCancelable;

    public ShowListDialogEvent(final int id, final String title, final String message, final ArrayList<String> list, final Integer[] selected, final boolean multiselect, final int button_positive, final int button_negative, final boolean setCancelable) {
        mId = id;
        mTitle = title;
        mMessage = message;
        mList = list;
        if (selected != null) {
            mSelected = selected;
        }
        mButtonPositive = button_positive;
        mButtonNegative = button_negative;
        mMultiselect = multiselect;
        mSetCancelable = setCancelable;
    }

    public ShowListDialogEvent(final int id, final String title, final String message, final ArrayList<String> list, final int button_positive, final int button_negative, final boolean setCancelable) {
        mId = id;
        mTitle = title;
        mMessage = message;
        mList = list;
        mButtonPositive = button_positive;
        mButtonNegative = button_negative;
        mMultiselect = false;
        mSetCancelable = setCancelable;
    }

    public String getMessage() {
        return mMessage;
    }

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getButtonPositive() {
        return mButtonPositive;
    }

    public int getButtonNegative() {
        return mButtonNegative;
    }

    public ArrayList<String> getList() {
        return mList;
    }

    public Integer[] getSelected() {
        return mSelected;
    }

    public boolean isMultiselect() {
        return mMultiselect;
    }

    public boolean isCancelable() {
        return mSetCancelable;
    }


}
