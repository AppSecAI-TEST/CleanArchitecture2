package com.cleanarchitecture.shishkin.api.repository;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.persistence.room.RoomDatabase;

import com.cleanarchitecture.shishkin.api.controller.IModule;
import com.cleanarchitecture.shishkin.api.data.AbstractViewModel;

/**
 * The interface Db provider.
 */
public interface IDbProvider<H extends AbstractViewModel> extends IModule {

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
     * Получить БД
     *
     * @param <T>          the type parameter
     * @param klass        Class объекта БД
     * @param databaseName имя БД
     * @return the db      объект БД
     */
    <T extends RoomDatabase> T getDb(final Class<T> klass, final String databaseName);

    /**
     * Зарегестрировать слушателя ViewModel
     *
     * @param <T>           the type parameter
     * @param activity      the activity
     * @param nameViewModel the name view model
     * @param klass         the klass
     * @param observer      the observer
     */
    <T> void observe(LifecycleActivity activity, String nameViewModel, Class<H> klass, IObserver<T> observer);

    /**
     * Удалить слушателя ViewModel
     *
     * @param <T>           the type parameter
     * @param nameViewModel the name view model
     * @param observer      the observer
     */
    <T> void removeObserver(final String nameViewModel, final IObserver<T> observer);

    /**
     * Удалить ViewModel
     *
     * @param nameViewModel the name view model
     */
    void removeViewModel(String nameViewModel);

    /**
     * Получить ViewModel
     *
     * @param nameViewModel the name view model
     * @return the view model
     */
    H getViewModel(final String nameViewModel);
}
