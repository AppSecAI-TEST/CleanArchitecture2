package com.cleanarchitecture.shishkin.api.repository;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.persistence.room.RoomDatabase;

import com.cleanarchitecture.shishkin.api.controller.IModule;
import com.cleanarchitecture.shishkin.api.model.AbstractViewModel;

/**
 * The interface Db provider.
 */
public interface IDbProvider<H extends AbstractViewModel, T extends RoomDatabase> extends IModule {

    /**
     * архивировать БД
     *
     * @param databaseName имя БД
     * @param dirBackup    каталог архивирования
     */
    void backup(String databaseName, String dirBackup);

    /**
     * Востановить БД
     *
     * @param databaseName имя БД
     * @param dirBackup    каталог с архивом БД
     */
    void restore(String databaseName, String dirBackup);

    /**
     * Получить БД
     *
     * @param klass        Class объекта БД
     * @param databaseName имя БД
     * @return the db      объект БД
     */
    T getDb(final Class<T> klass, final String databaseName);

    /**
     * Зарегестрировать слушателя ViewModel
     *
     * @param <E>           the type parameter
     * @param activity      the activity
     * @param nameViewModel the name view model
     * @param klass         the klass
     * @param observer      the observer
     */
    <E> void observe(LifecycleActivity activity, String nameViewModel, Class<H> klass, IObserver<E> observer);

    /**
     * Удалить слушателя ViewModel
     *
     * @param <E>           the type parameter
     * @param nameViewModel the name view model
     * @param observer      the observer
     */
    <E> void removeObserver(final String nameViewModel, final IObserver<E> observer);

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
