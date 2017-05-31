package com.cleanarchitecture.shishkin.application.database.item;

import android.os.Parcel;

import java.io.Serializable;

public class SqliteMasterItem implements Serializable {

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

    public SqliteMasterItem setType(String type) {
        this.mType = type;
        return this;
    }

    public SqliteMasterItem setName(String name) {
        this.mName = name;
        return this;
    }

    public SqliteMasterItem setTblName(String tblName) {
        this.mTblName = tblName;
        return this;
    }

}
