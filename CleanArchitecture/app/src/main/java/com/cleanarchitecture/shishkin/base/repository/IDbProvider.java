package com.cleanarchitecture.shishkin.base.repository;

import android.arch.persistence.room.RoomDatabase;

import com.cleanarchitecture.shishkin.base.controller.ISubscriber;

public interface IDbProvider extends ISubscriber {

    /**
     * архивировать БД
     *
     * @param <T>          the type parameter
     * @param databaseName имя БД
     * @param dirBackup    каталог архивирования
     */
    <T extends RoomDatabase> void backup(String databaseName, String dirBackup);

    /**
     * Востановить БД
     *
     * @param <T>          the type parameter
     * @param databaseName имя БД
     * @param dirBackup    каталог с архивом БД
     */
    <T extends RoomDatabase> void restore(String databaseName, String dirBackup);

    /**
     * Gets db.
     *
     * @param <T>          the type parameter
     * @param klass        Class объекта БД
     * @param databaseName имя БД
     * @return the db      объект БД
     */
    <T extends RoomDatabase> T getDb(final Class<T> klass, final String databaseName);
}
