package com.cleanarchitecture.shishkin.base.ui.activity;

import android.support.annotation.IdRes;
import android.view.View;

import com.cleanarchitecture.shishkin.base.controller.ISubscriber;
import com.cleanarchitecture.shishkin.base.presenter.ActivityPresenter;
import com.cleanarchitecture.shishkin.base.presenter.IPresenter;

/**
 * Интерфейс activity
 */
@SuppressWarnings("unused")
public interface IActivity extends ISubscriber {
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
     * Получить ActivityPresenter activity.
     *
     * @return ActivityPresenter activity
     */
    ActivityPresenter getActivityPresenter();

}
