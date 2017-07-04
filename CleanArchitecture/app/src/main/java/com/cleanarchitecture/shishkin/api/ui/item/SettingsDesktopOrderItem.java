package com.cleanarchitecture.shishkin.api.ui.item;

import android.os.Parcel;
import android.os.Parcelable;

import com.cleanarchitecture.shishkin.common.content.dao.IIdentify;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class SettingsDesktopOrderItem implements IIdentify<String>, Parcelable {
    @SerializedName("Id")
    private String mId;

    @SerializedName("Enabled")
    private boolean mEnabled;

    @SerializedName("Tag")
    private String mTag;

    public SettingsDesktopOrderItem(final String id) {
        mId = id;
    }

    public SettingsDesktopOrderItem(final String id, final String tag, final boolean enabled) {
        mId = id;
        mTag = tag;
        mEnabled = enabled;
    }

    public SettingsDesktopOrderItem(final Parcel src) {
        mId = (String) src.readValue(String.class.getClassLoader());
        mTag = (String) src.readValue(String.class.getClassLoader());
        mEnabled = (boolean) src.readValue(Boolean.class.getClassLoader());
    }


    @Override
    public String getId() {
        return mId;
    }

    public SettingsDesktopOrderItem setId(final String id) {
        mId = id;
        return this;
    }

    public String getTag() {
        return mTag;
    }

    public SettingsDesktopOrderItem setTag(final String tag) {
        mTag = tag;
        return this;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public SettingsDesktopOrderItem setEnabled(final boolean enabled) {
        mEnabled = enabled;
        return this;
    }

    @Override
    public int hashCode() {
        return mId.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        } else {
            final SettingsDesktopOrderItem that = (SettingsDesktopOrderItem) o;
            return mId.equals(that.mId);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeValue(mId);
        dest.writeValue(mTag);
        dest.writeValue(mEnabled);
    }

    public static final Creator<SettingsDesktopOrderItem> CREATOR = new Creator<SettingsDesktopOrderItem>() {
        @Override
        public SettingsDesktopOrderItem createFromParcel(final Parcel source) {
            return new SettingsDesktopOrderItem(source);
        }

        @Override
        public SettingsDesktopOrderItem[] newArray(final int size) {
            return new SettingsDesktopOrderItem[size];
        }
    };

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
