package com.cleanarchitecture.shishkin.base.ui.activity;

import android.support.annotation.IdRes;
import android.view.View;

import com.cleanarchitecture.shishkin.base.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.base.event.BackpressActivityEvent;
import com.cleanarchitecture.shishkin.base.event.ClearBackStackEvent;
import com.cleanarchitecture.shishkin.base.event.FinishActivityEvent;
import com.cleanarchitecture.shishkin.base.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.base.event.ui.DialogResultEvent;
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
     * @return Activity activity
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

    /**
     * Событие - finish, указанной Activity
     *
     * @param event событие
     */
    void onFinishActivityEvent(final FinishActivityEvent event);

    /**
     * Событие - Backpress, указанной Activity
     *
     * @param event событие
     */
    void onBackpressActivityEvent(final BackpressActivityEvent event);

    /**
     * Событие - очистить Back Stack
     *
     * @param event событие
     */
    void onClearBackStackEvent(ClearBackStackEvent event);

    /**
     * Событие - finish приложения
     *
     * @param event событие
     */
    void onFinishApplicationEvent(FinishApplicationEvent event);

    /**
     * Событие - закрыт диалог
     *
     * @param event событие
     */
    void onDialogResultEvent(DialogResultEvent event);

}
