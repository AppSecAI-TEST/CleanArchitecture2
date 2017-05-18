package com.cleanarchitecture.shishkin.base.storage;

import com.cleanarchitecture.shishkin.base.controller.ISubscriber;

import java.io.Serializable;

public interface IStorage extends ISubscriber {

    /**
     * Put value to storage.
     *
     * @param key   the key
     * @param value the value
     */
    void put(String key, Serializable value);

    /**
     * Get value from storage.
     *
     * @param key the key
     * @return the serializable
     */
    Serializable get(String key);

    /**
     * Get value from storage.
     *
     * @param key the key
     * @param defaultValue the default value
     * @return the serializable
     */
    Serializable get(final String key, final Serializable defaultValue);

    /**
     * Clear value.
     *
     * @param key the key
     */
    void clear(String key);

    /**
     * Clear all values.
     */
    void clearAll();
}
