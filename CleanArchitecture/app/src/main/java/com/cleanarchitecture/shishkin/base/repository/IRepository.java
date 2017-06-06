package com.cleanarchitecture.shishkin.base.repository;

import com.cleanarchitecture.shishkin.base.controller.IModule;

import java.io.Serializable;

/**
 * The interface Repository.
 */
public interface IRepository extends IModule {

    /**
     * Получить данные из кэша
     *
     * @param key       ключ данных
     * @param cacheType тип кеширования
     * @return возвращаемые Serializable данные
     */
    Serializable getFromCache(String key, int cacheType);

    /**
     * Сохранить данные в кэше.
     *
     * @param key       ключ данных
     * @param cacheType тип кеширования
     * @param value     сохраняемые Serializable данные
     */
    void putToCache(String key, int cacheType, Serializable value);

    /**
     * Получить net provider.
     *
     * @return net provider
     */
    INetProvider getNetProvider();

    /**
     * Получить content provider.
     *
     * @return content provider
     */
    IContentProvider getContentProvider();

    /**
     * Получить db provider.
     *
     * @return db provider
     */
    IDbProvider getDbProvider();

}
