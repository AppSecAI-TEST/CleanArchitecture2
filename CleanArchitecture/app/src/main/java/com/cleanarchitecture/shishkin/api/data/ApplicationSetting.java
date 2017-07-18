package com.cleanarchitecture.shishkin.api.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ApplicationSetting implements Parcelable {
    public static final int TYPE_LIST = 0;
    public static final int TYPE_SWITCH = 1;
    public static final int TYPE_TEXT = 2;
    public static final int TYPE_COLOR = 3;

    private ArrayList<String> mValues;
    private String mCurrentValue;
    private String mDefaultValue;
    private String mPreferenceName;
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

    public String getPreferenceName() {
        return mPreferenceName;
    }

    public ApplicationSetting setPreferenceName(String preferenceName) {
        this.mPreferenceName = preferenceName;
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

    public String getDefaultValue() {
        return mDefaultValue;
    }

    public ApplicationSetting setDefaultValue(String defaultValue) {
        this.mDefaultValue = defaultValue;
        return this;
    }

    public int getType() {
        return mType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.mValues);
        dest.writeString(this.mCurrentValue);
        dest.writeString(this.mDefaultValue);
        dest.writeString(this.mPreferenceName);
        dest.writeInt(this.mTitleId);
        dest.writeInt(this.mId);
        dest.writeInt(this.mType);
    }

    protected ApplicationSetting(Parcel in) {
        this.mValues = in.createStringArrayList();
        this.mCurrentValue = in.readString();
        this.mDefaultValue = in.readString();
        this.mPreferenceName = in.readString();
        this.mTitleId = in.readInt();
        this.mId = in.readInt();
        this.mType = in.readInt();
    }

    public static final Parcelable.Creator<ApplicationSetting> CREATOR = new Parcelable.Creator<ApplicationSetting>() {
        @Override
        public ApplicationSetting createFromParcel(Parcel source) {
            return new ApplicationSetting(source);
        }

        @Override
        public ApplicationSetting[] newArray(int size) {
            return new ApplicationSetting[size];
        }
    };
}
