package com.cleanarchitecture.shishkin.base.repository;

import com.cleanarchitecture.shishkin.base.controller.ISubscriber;

import java.io.Serializable;

/**
 * The interface Repository.
 */
public interface IRepository extends ISubscriber {

    /**
     * Получить данные из кэша
     *
     * @param key       ключ данных
     * @param cacheType тип кеша
     * @return возвращаемые Serializable данные
     */
    Serializable getFromCache(String key, int cacheType);

    /**
     * Сохранить данные в кэше.
     *
     * @param key       ключ данных
     * @param cacheType тип кеша
     * @param value     сохраняемые Serializable данные
     */
    void putToCache(String key, int cacheType, Serializable value);

    /**
     * Получить net provider.
     *
     * @return net provider
     */
    NetProvider getNetProvider();

    /**
     * Получить content provider.
     *
     * @return content provider
     */
    ContentProvider getContentProvider();

    /**
     * Получить db provider.
     *
     * @return db provider
     */
    DbProvider getDbProvider();

}
