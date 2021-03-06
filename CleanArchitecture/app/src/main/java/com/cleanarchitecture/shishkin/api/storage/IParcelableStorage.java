package com.cleanarchitecture.shishkin.api.storage;

import android.os.Parcelable;

import com.cleanarchitecture.shishkin.api.controller.ISubscriber;

import java.util.List;

public interface IParcelableStorage<T extends Parcelable> extends ISubscriber {

    /**
     * Put value to storage.
     *
     * @param key   the key
     * @param value the value
     */
    void put(String key, T value);

    /**
     * Put list of values to storage.
     *
     * @param key    the key
     * @param values the values
     */
    void put(String key, List<T> values);

    /**
     * Get value from storage.
     *
     * @param key       the key
     * @param itemClass the value class
     * @return the Parcelable
     */
    T get(String key, Class itemClass);

    /**
     * Get list of values from storage.
     *
     * @param key       the key
     * @param itemClass the value class
     * @return the list of Parcelable
     */
    List<T> getList(String key, Class itemClass);

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
