package com.cleanarchitecture.shishkin.application.database.item;

import android.os.Parcel;
import android.os.Parcelable;

public class SqliteMasterItem implements Parcelable {

    private String mType;
    private String mName;
    private String mTblName;

    public SqliteMasterItem() {
    }

    public SqliteMasterItem(final Parcel src) {
        mType = (String) src.readValue(String.class.getClassLoader());
        mName = (String) src.readValue(Integer.class.getClassLoader());
        mTblName = (String) src.readValue(String.class.getClassLoader());
    }

    public String getType() {
        return mType;
    }

    public String getName() {
        return mName;
    }

    public String getTblName() {
        return mTblName;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setTblName(String tblName) {
        this.mTblName = tblName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeValue(mType);
        dest.writeValue(mName);
        dest.writeValue(mTblName);
    }

    public static final Creator<SqliteMasterItem> CREATOR = new Creator<SqliteMasterItem>() {
        @Override
        public SqliteMasterItem createFromParcel(final Parcel source) {
            return new SqliteMasterItem(source);
        }

        @Override
        public SqliteMasterItem[] newArray(final int size) {
            return new SqliteMasterItem[size];
        }
    };

}
