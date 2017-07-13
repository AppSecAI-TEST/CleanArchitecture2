package com.cleanarchitecture.shishkin.api.repository.data;

import java.util.ArrayList;

public class ApplicationSetting {
    public static final int TYPE_LIST = 0;
    public static final int TYPE_SWITCH = 1;
    public static final int TYPE_TEXT = 2;
    public static final int TYPE_COLOR = 3;

    private ArrayList<String> mValues;
    private String mCurrentValue;
    private int mTitleId;
    private int mId;
    private int mType = 0;


    public ApplicationSetting(int type) {
        mType = type;
    }

    public int getId() {
        return mId;
    }

    public ApplicationSetting setId(int id) {
        this.mId = id;
        return this;
    }

    public int getTitleId() {
        return mTitleId;
    }

    public ApplicationSetting setTitleId(int id) {
        this.mTitleId = id;
        return this;
    }

    public ArrayList<String> getValues() {
        return mValues;
    }

    public ApplicationSetting setValues(ArrayList<String> values) {
        this.mValues = values;
        return this;
    }

    public String getCurrentValue() {
        return mCurrentValue;
    }

    public ApplicationSetting setCurrentValue(String currentValue) {
        this.mCurrentValue = currentValue;
        return this;
    }

    public int getType() {
        return mType;
    }

}
