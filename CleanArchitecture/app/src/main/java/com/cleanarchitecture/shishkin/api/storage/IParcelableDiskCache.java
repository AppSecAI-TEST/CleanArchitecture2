package com.cleanarchitecture.shishkin.api.storage;

import android.os.Parcelable;

import java.util.List;

public interface IParcelableDiskCache<T extends Parcelable> {

    void put(String key, Parcelable value);

    void put(String key, Parcelable value, long expired);

    void put(String key, List<T> values);

    void put(String key, List<T> values, long expired);

    T get(String key, Class itemClass);

    List<T> getList(String key, Class itemClass);

    void clear(String key);

    void clear();

    void setVersion(int version);

    void flush();

    void close();

}
