package com.cleanarchitecture.shishkin.base.ui.activity;

import android.support.annotation.IdRes;
import android.view.View;

import com.cleanarchitecture.shishkin.base.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.base.presenter.IPresenter;

import butterknife.Unbinder;

/**
 * Интерфейс activity
 */
@SuppressWarnings("unused")
public interface IActivity extends IModuleSubscriber {
    /**
     * Найти view в activity
     *
     * @param <V> the type view
     * @param id  the id view
     * @return the view
     */
    <V extends View> V findView(@IdRes final int id);

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
     * Получить Activity
     *
     * @return Activity
     */
    AbstractActivity getActivity();

    /**
     * Установить цвет status bar телефона
     *
     * @param color цвет Status Bar
     */
    void setStatusBarColor(final int color);

    /**
     * Lock orientation.
     */
    void lockOrientation();

    /**
     * Unlock orientation.
     */
    void unlockOrientation();

    /**
     * Проверить Activity
     *
     * @return true - если Activity находиться в рабочем состоянии
     */
    boolean validate();

}
