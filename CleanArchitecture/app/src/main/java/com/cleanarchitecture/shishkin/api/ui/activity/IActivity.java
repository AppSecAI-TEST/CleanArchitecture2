package com.cleanarchitecture.shishkin.api.ui.activity;

import android.support.annotation.IdRes;
import android.view.View;

import com.cleanarchitecture.shishkin.api.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.api.event.BackpressActivityEvent;
import com.cleanarchitecture.shishkin.api.event.ClearBackStackEvent;
import com.cleanarchitecture.shishkin.api.event.FinishActivityEvent;
import com.cleanarchitecture.shishkin.api.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.api.event.ui.DialogResultEvent;
import com.cleanarchitecture.shishkin.api.presenter.IPresenter;

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

    /**
     * Установить флаг, очищать данные о состоянии презентеров
     *
     * @param lostStateData true - очищать данные
     */
    void setLostStateData(boolean lostStateData);
}
