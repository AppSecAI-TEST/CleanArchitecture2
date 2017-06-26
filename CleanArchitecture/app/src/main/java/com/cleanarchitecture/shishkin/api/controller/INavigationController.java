package com.cleanarchitecture.shishkin.api.controller;

import com.cleanarchitecture.shishkin.api.event.OnActivityBackPressedEvent;
import com.cleanarchitecture.shishkin.api.event.ShowFragmentEvent;
import com.cleanarchitecture.shishkin.api.event.StartActivityEvent;
import com.cleanarchitecture.shishkin.api.event.SwitchToFragmentEvent;
import com.cleanarchitecture.shishkin.api.ui.activity.AbstractActivity;
import com.cleanarchitecture.shishkin.api.ui.activity.AbstractContentActivity;

/**
 * Контроллер навигации приложения.
 */
@SuppressWarnings("unused")
public interface INavigationController extends IController<INavigationSubscriber> {

    /**
     * Получить фрагмент по его id.
     *
     * @param <F> тип фрагмента
     * @param cls класс фрагмента
     * @param id  the id
     * @return фрагмент
     */
    <F> F getFragment(final Class<F> cls, final int id);

    /**
     * Получить ContentFragment
     *
     * @param <F> тип фрагмента
     * @param cls класс фрагмента
     * @return ContentFragment
     */
    <F> F getContentFragment(final Class<F> cls);

    /**
     * Обрабатывает событие - показать фрагмент
     *
     * @param event событие
     */
    void onShowFragmentEvent(ShowFragmentEvent event);

    /**
     * Обрабатывает событие - переключиться на фрагмент
     *
     * @param event событие
     */
    void onSwitchToFragmentEvent(SwitchToFragmentEvent event);

    /**
     * Обрабатывает событие - нажатие на BackPress
     *
     * @param event событие
     */
    void onActivityBackPressedEvent(OnActivityBackPressedEvent event);

    /**
     * Получить AbstractActivity
     *
     * @return the AbstractActivity
     */
    AbstractActivity getActivity();

    /**
     * Получить AbstractActivity
     *
     * @param name имя activity
     * @return the AbstractActivity
     */
    AbstractActivity getActivity(String name);

    /**
     * Получить AbstractContentActivity
     *
     * @return AbstractContentActivity
     */
    AbstractContentActivity getContentActivity();

    /**
     * Получить AbstractContentActivity
     *
     * @param name имя activity
     * @return AbstractContentActivity
     */
    AbstractContentActivity getContentActivity(String name);

    /**
     * Обрабатывает событие - Start Activity
     *
     * @param event событие
     */
    void onStartActivityEvent(final StartActivityEvent event);

}
