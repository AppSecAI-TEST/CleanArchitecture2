package com.cleanarchitecture.shishkin.common.content.dao;

/**
 * Interface to any data entity class that can be inserted, updated or deleted
 * by using {@link AbstractIdentifyDAO} implementation.
 */
public interface IIdentify<K> {

    /**
     * Returns an entity primary key as is.
     */
    K getId();

}
