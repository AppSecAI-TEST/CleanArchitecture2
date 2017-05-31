package com.cleanarchitecture.shishkin.base.ui.fragment;

import android.support.annotation.IdRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cleanarchitecture.shishkin.base.controller.ISubscriber;
import com.cleanarchitecture.shishkin.base.presenter.IPresenter;
import com.cleanarchitecture.shishkin.base.ui.activity.IActivity;

import butterknife.Unbinder;

/**
 * Интерфейс фрагмента
 */
@SuppressWarnings("unused")
public interface IFragment extends ISubscriber {
    /**
     * Найти view во фрагменте
     *
     * @param <V> the type view
     * @param id  the id view
     * @return the view
     */
    <V extends View> V findView(@IdRes final int id);

    /**
     * получить IActivitySubscriber фрагмента
     *
     * @return IActivitySubscriber фрагмента
     */
    IActivity getActivitySubscriber();

    /**
     * Зарегистрировать presenter.
     *
     * @param presenter the presenter
     */
    void registerPresenter(final IPresenter presenter);

    /**
     * Получить presenter.
     *
     * @param name имя presenter
     * @return the presenter
     */
    IPresenter getPresenter(final String name);

    /**
     * Получить AppCompatActivity activity фрагмента.
     *
     * @return the AppCompatActivity activity
     */
    AppCompatActivity getAppCompatActivity();

    /**
     * Обновить данные во фрагменте
     */
    void refreshData();

    /**
     * Обновить views во фрагменте
     */
    void refreshViews();

    /**
     * Получить Butter Knife Unbinder
     *
     * @return Unbinder the unbinder
     */
    Unbinder getUnbinder();

    /**
     * Установить Butter Knife Unbinder
     *
     * @param unbinder the unbinder
     */
    void setUnbinder(Unbinder unbinder);

    /**
     * Проверить Fragment
     *
     * @return true - если Fragment находиться в рабочем состоянии
     */
    boolean validate();

    /**
     * Показать progress bar.
     */
    void showProgressBar();

    /**
     * Скрыть progress bar.
     */
    void hideProgressBar();

}
