package com.cleanarchitecture.shishkin.api.storage;

import android.os.Parcelable;

public interface IExpiredParcelableStorage<T extends Parcelable> extends IParcelableStorage<T> {

    /**
     * Put value to storage.
     *
     * @param key     the key
     * @param value   the value
     * @param expired expired date
     */
    void put(String key, T value, long expired);

}
