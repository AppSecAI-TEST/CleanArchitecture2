package com.cleanarchitecture.shishkin.api.controller;

import com.cleanarchitecture.shishkin.api.event.OnActivityBackPressedEvent;
import com.cleanarchitecture.shishkin.api.event.ShowFragmentEvent;
import com.cleanarchitecture.shishkin.api.event.SwitchToFragmentEvent;

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
}
