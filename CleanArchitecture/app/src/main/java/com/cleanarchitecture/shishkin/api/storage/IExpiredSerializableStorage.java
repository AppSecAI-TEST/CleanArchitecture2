package com.cleanarchitecture.shishkin.api.storage;

import java.io.Serializable;

public interface IExpiredSerializableStorage extends ISerializableStorage {

    /**
     * Put value to storage.
     *
     * @param key     the key
     * @param value   the value
     * @param expired expired date
     */
    void put(String key, Serializable value, long expired);

    /**
     * Check expired period all keys.
     */
    void check();
}
