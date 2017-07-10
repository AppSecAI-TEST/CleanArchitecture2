package com.cleanarchitecture.shishkin.api.storage;

import com.cleanarchitecture.shishkin.api.controller.ISubscriber;

import java.io.Serializable;
import java.util.List;

public interface ISerializableStorage extends ISubscriber {

    /**
     * Put value to storage.
     *
     * @param key   the key
     * @param value the value
     */
    void put(String key, Serializable value);

    /**
     * Put values to storage.
     *
     * @param key    the key
     * @param values the list of Serializable
     */
    void put(String key, List<Serializable> values);

    /**
     * Get value from storage.
     *
     * @param key the key
     * @return the serializable
     */
    Serializable get(String key);

    /**
     * Get values from storage.
     *
     * @param key the key
     * @return the list of serializable
     */
    List<Serializable> getList(String key);

    /**
     * Get value from storage.
     *
     * @param key          the key
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
    void clear();
}
