package com.cleanarchitecture.shishkin.application.database.item;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "Contact")
public class Contact {
    @SerializedName("RowId")
    @ColumnInfo(name = "RowId")
    @PrimaryKey
    public String RowId;

    @SerializedName("Name")
    @ColumnInfo(name = "Name")
    public String Name;

    @SerializedName("Phones")
    @ColumnInfo(name = "Phones")
    public String Phones;

    @SerializedName("Photo")
    @ColumnInfo(name = "Photo")
    public String Photo;

    public Contact setRowId(final String rowId) {
        RowId = rowId;
        return this;
    }

    public Contact setName(final String name) {
        Name = name;
        return this;
    }

    public Contact setPhones(final String phones) {
        Phones = phones;
        return this;
    }

    public Contact setPhoto(final String photo) {
        Photo = photo;
        return this;
    }
}
