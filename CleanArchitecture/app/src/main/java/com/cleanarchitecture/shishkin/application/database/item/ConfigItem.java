package com.cleanarchitecture.shishkin.application.database.item;

import com.cleanarchitecture.shishkin.base.database.dao.IIdentify;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ConfigItem implements IIdentify<String>, Serializable {

    @SerializedName("RowId")
    private String mRowId;

    @SerializedName("Version")
    private int mVersion;

    public ConfigItem() {
    }

    public ConfigItem(String rowId, int version) {
        mRowId = rowId;
        mVersion = version;
    }

    @Override
    public String getId() {
        return mRowId;
    }

    public ConfigItem setId(final String id) {
        mRowId = id;
        return this;
    }

    public int getVersion() {
        return mVersion;
    }

    public ConfigItem setVersion(final int text) {
        mVersion = text;
        return this;
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
}
