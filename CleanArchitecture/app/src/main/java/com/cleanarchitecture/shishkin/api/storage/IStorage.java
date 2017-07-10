package com.cleanarchitecture.shishkin.api.storage;

import java.util.List;

public interface IStorage<T> {

    /**
     * Put value to storage.
     *
     * @param key   the key
     * @param value the value
     */
    void put(String key, T value);

    /**
     * Put values to storage.
     *
     * @param key    the key
     * @param values the list of values
     */
    void put(String key, List<T> values);

    /**
     * Get value from storage.
     *
     * @param key the key
     * @return the value
     */
    T get(String key);

    /**
     * Get values from storage.
     *
     * @param key the key
     * @return the list of values
     */
    List<T> getList(String key);

    /**
     * Get value from storage.
     *
     * @param key          the key
     * @param defaultValue the default value
     * @return the value
     */
    T get(final String key, final T defaultValue);

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
