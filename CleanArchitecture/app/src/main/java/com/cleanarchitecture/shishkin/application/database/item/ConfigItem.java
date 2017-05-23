package com.cleanarchitecture.shishkin.application.database.item;

import android.os.Parcel;
import android.os.Parcelable;

import com.cleanarchitecture.shishkin.base.database.dao.IIdentify;

public class ConfigItem implements IIdentify<String>, Parcelable  {

    private String mRowId;
    private int mVersion;

    public ConfigItem() {
    }

    public ConfigItem(String rowId, int version) {
        mRowId = rowId;
        mVersion = version;
    }

    public ConfigItem(final Parcel src) {
        mRowId = (String) src.readValue(String.class.getClassLoader());
        mVersion = (int) src.readValue(Integer.class.getClassLoader());
    }

    @Override
    public String getId() {
        return mRowId;
    }

    public void setId(final String id) {
        mRowId = id;
    }

    public int getVersion() {
        return mVersion;
    }

    public void setVersion(final int text) {
        mVersion = text;
    }

    @Override
    public int hashCode() {
        return mRowId.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        } else {
            final ConfigItem that = (ConfigItem) o;
            return mRowId.equals(that.mRowId);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeValue(mRowId);
        dest.writeValue(mVersion);
    }

    public static final Creator<ConfigItem> CREATOR = new Creator<ConfigItem>() {
        @Override
        public ConfigItem createFromParcel(final Parcel source) {
            return new ConfigItem(source);
        }

        @Override
        public ConfigItem[] newArray(final int size) {
            return new ConfigItem[size];
        }
    };

}
