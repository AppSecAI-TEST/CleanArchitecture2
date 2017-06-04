package com.cleanarchitecture.shishkin.base.controller;

/**
 * Контроллер навигации приложения.
 */
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

}
