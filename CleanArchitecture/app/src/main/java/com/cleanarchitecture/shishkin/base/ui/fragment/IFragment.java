package com.cleanarchitecture.shishkin.base.ui.fragment;

import android.arch.lifecycle.LifecycleActivity;
import android.support.annotation.IdRes;
import android.view.View;

import com.cleanarchitecture.shishkin.base.controller.ISubscriber;
import com.cleanarchitecture.shishkin.base.presenter.IPresenter;
import com.cleanarchitecture.shishkin.base.ui.IView;
import com.cleanarchitecture.shishkin.base.ui.activity.IActivity;

import butterknife.Unbinder;

/**
 * Интерфейс фрагмента
 */
@SuppressWarnings("unused")
public interface IFragment extends ISubscriber, IView {
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
     * Получить LifecycleActivity activity фрагмента.
     *
     * @return the LifecycleActivity activity
     */
    LifecycleActivity getLifecycleActivity();

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

}
