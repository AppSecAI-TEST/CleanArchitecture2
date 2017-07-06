package com.cleanarchitecture.shishkin.api.storage;

import android.os.Parcelable;

import java.util.List;

public interface IExpiredParcelableStorage<T extends Parcelable> extends IParcelableStorage<T> {

    /**
     * Put value to storage.
     *
     * @param key     the key
     * @param value   the value
     * @param expired expired date
     */
    void put(String key, T value, long expired);

    /**
     * Put value to storage.
     *
     * @param key     the key
     * @param values  the values list
     * @param expired expired date
     */
    void put(String key, List<T> values, long expired);

}
