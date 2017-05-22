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
     * @param key ключ данных
     * @return возвращаемые Serializable данные
     */
    Serializable getFromCache(int key);

    /**
     * Сохранить данные в кэше.
     *
     * @param key   ключ данных
     * @param value сохраняемые Serializable данные
     */
    void putToCache(int key, Serializable value);

    /**
     * Установить тип кэширования данных по умолчанию
     *
     * @param defaultCaching the default caching
     */
    void setDefaultCaching(int defaultCaching);

    /**
     * Получить тип кеширования данных
     *
     * @param key ключ данных
     * @return тип кэширования данных
     */
    int getTypeCached(int key);

    /**
     * Установить тип кэширования данных
     *
     * @param key ключ данных
     * @param cacheType тип кэширования данных
     * @return репозиторий
     */
    Repository setTypeCached(int key, int cacheType);

}
