package com.cleanarchitecture.shishkin.application.data.item;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PhoneContactItem implements Parcelable {

    @SerializedName("RowId")
    private String mRowId;

    @SerializedName("Name")
    private String mName;

    @SerializedName("Phones")
    private String mPhones;

    @SerializedName("Photo")
    private String mPhoto;

    public PhoneContactItem() {
    }

    public PhoneContactItem(final Parcel src) {
        mRowId = (String) src.readValue(String.class.getClassLoader());
        mName = (String) src.readValue(String.class.getClassLoader());
        mPhones = (String) src.readValue(String.class.getClassLoader());
        mPhoto = (String) src.readValue(String.class.getClassLoader());
    }

    public String getId() {
        return mRowId;
    }

    public PhoneContactItem setId(final String id) {
        mRowId = id;
        return this;
    }

    public String getName() {
        return mName;
    }

    public PhoneContactItem setName(final String text) {
        mName = text;
        return this;
    }

    public String getPhones() {
        return mPhones;
    }

    public PhoneContactItem setPhones(String phones) {
        this.mPhones = phones;
        return this;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public PhoneContactItem setPhoto(String photo) {
        this.mPhoto = photo;
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
            final PhoneContactItem that = (PhoneContactItem) o;
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
        dest.writeValue(mName);
        dest.writeValue(mPhones);
        dest.writeValue(mPhoto);
    }

    public static final Creator<PhoneContactItem> CREATOR = new Creator<PhoneContactItem>() {
        @Override
        public PhoneContactItem createFromParcel(final Parcel source) {
            return new PhoneContactItem(source);
        }

        @Override
        public PhoneContactItem[] newArray(final int size) {
            return new PhoneContactItem[size];
        }
    };

    public String toString() {
        return "RowId:" + mRowId + "; Name:" + mName + "; Phones:" + mPhones;
    }

}
