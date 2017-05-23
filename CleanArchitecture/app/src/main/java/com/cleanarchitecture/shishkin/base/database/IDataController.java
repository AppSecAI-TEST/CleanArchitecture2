package com.cleanarchitecture.shishkin.base.database;

import android.content.Context;

/**
 * Интерфейс работы с БД.
 */
public interface IDataController {

    /**
     * Создать БД
     *
     * @param context the context
     */
    void create(Context context);

    /**
     * Резервировать БД
     *
     * @param path путь к резервной копии БД
     * @return the boolean - флаг успешности завершения операции
     */
    boolean backup(String path);

    /**
     * Восстановить БД
     *
     * @param path путь к резервной копии БД
     * @return the boolean - флаг успешности завершения операции
     */
    boolean restore(String path);

    /**
     * Получить версию БД
     *
     * @return версия БД
     */
    int getVersion();

    /**
     * Контролировать наличие БД
     *
     * @param context the context
     * @param name    имя БД
     * @return the boolean - true - БД существует, false - БД не существует
     */
    boolean exists(final Context context, final String name);

}
