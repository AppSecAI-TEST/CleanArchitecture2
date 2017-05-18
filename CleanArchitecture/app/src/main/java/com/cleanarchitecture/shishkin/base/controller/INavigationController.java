package com.cleanarchitecture.shishkin.base.controller;

/**
 * Контроллер навигации приложения.
 */
public interface INavigationController extends ISubscriber {

    /**
     * Зарегистрировать подписчика
     *
     * @param subscriber подписчик
     */
    void register(INavigationSubscriber subscriber);

    /**
     * Отключить подписчика
     *
     * @param subscriber подписчик
     */
    void unregister(INavigationSubscriber subscriber);

    /**
     * Получить подписчика
     *
     * @return подписчик
     */
    INavigationSubscriber getSubscriber();

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
     * Установить текущего подписчика
     *
     * @param subscriber подписчик
     */
    void setCurrentSubscriber(INavigationSubscriber subscriber);

}
