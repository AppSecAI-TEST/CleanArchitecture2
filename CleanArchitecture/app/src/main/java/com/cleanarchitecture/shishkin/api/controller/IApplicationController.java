package com.cleanarchitecture.shishkin.api.controller;

import android.content.Context;

/**
 * The interface Application controller.
 */
public interface IApplicationController extends IModule {

    /**
     * Получить Context приложения
     *
     * @return Context приложения
     */
    Context getApplicationContext();

    /**
     * Получить путь хранения Cache на SDCard
     *
     * @return путь хранения Cache на SDCard
     */
    String getExternalCachePath();

    /**
     * Получить путь хранения данных и журналов на SDCard
     *
     * @return путь хранения данных и журналов на SDCard
     */
    String getExternalApplicationPath();

    /**
     * Получить Permisions приложения
     *
     * @return the string [ ]
     */
    String[] getRequiredPermisions();

    /**
     * Событие - приложение обновлено
     *
     * @param version версия приложения
     */
    void onApplicationUpdated(int version);

    /**
     * Получить версию приложения
     *
     * @return версия приложения
     */
    int getVersion();
}