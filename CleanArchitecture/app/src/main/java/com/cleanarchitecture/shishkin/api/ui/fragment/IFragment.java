package com.cleanarchitecture.shishkin.api.ui.fragment;

import android.arch.lifecycle.LifecycleActivity;
import android.support.annotation.IdRes;
import android.view.View;

import com.cleanarchitecture.shishkin.api.controller.ISubscriber;
import com.cleanarchitecture.shishkin.api.presenter.IPresenter;
import com.cleanarchitecture.shishkin.api.ui.IView;
import com.cleanarchitecture.shishkin.api.ui.activity.IActivity;

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
     * Проверить Fragment
     *
     * @return true - если Fragment находиться в рабочем состоянии
     */
    boolean validate();

    /**
     * Показать Circle Progress Bar
     *
     * @param progress текущая позиция
     */
    void showCircleProgressBar(final float progress);

    /**
     * Скрыть Circle Progress Bar
     */
    void hideCircleProgressBar();

    /**
     * Показать подсказку
     *
     * @param anchorView anchor view
     * @param resId      id текста подсказки
     * @param gravity    выравнивание подсказки
     */
    void showTooltip(final View anchorView, final int resId, final int gravity);
}
