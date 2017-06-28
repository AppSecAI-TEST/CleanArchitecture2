package com.cleanarchitecture.shishkin.api.storage;

import com.cleanarchitecture.shishkin.api.controller.ISubscriber;

import java.io.Serializable;

public interface IExpiredStorage extends IStorage {

    /**
     * Put value to storage.
     *
     * @param key   the key
     * @param value the value
     * @param expired expired date
     */
    void put(String key, Serializable value, long expired);
}
