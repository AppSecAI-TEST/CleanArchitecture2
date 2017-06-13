package com.cleanarchitecture.shishkin.api.controller;

import android.support.v4.app.Fragment;

@SuppressWarnings("unused")
public interface INavigationSubscriber extends ISubscriber {

    /**
     * Переключиться на фрагмент
     *
     * @param name имя фрагмента
     */
    boolean switchToFragment(String name);

    /**
     * Показать фрагмент
     *
     * @param fragment фрагмент
     */
    void showFragment(Fragment fragment);

    /**
     * Показать фрагмент с allowingStateLoss
     *
     * @param fragment фрагмент
     */
    void showFragment(Fragment fragment, boolean allowingStateLoss);

    /**
     * Показать фрагмент
     *
     * @param fragment          фрагмент
     * @param addToBackStack    флаг - добавить в back stack
     * @param clearBackStack    флаг - очистить back stack
     * @param animate           флаг - использовать анимацию
     * @param allowingStateLoss флаг - разрешить allowingStateLoss
     */
    void showFragment(Fragment fragment, boolean addToBackStack, boolean clearBackStack,
                      boolean animate, boolean allowingStateLoss);

    /**
     * Событие -  on back pressed.
     */
    void onActivityBackPressed();

    /**
     * Получить фрагмент
     *
     * @param <F> тип фрагмента
     * @param cls класс фрагмента
     * @param id  the id
     * @return фрагмент
     */
    <F> F getFragment(Class<F> cls, final int id);

    /**
     * Получить content фрагмент.
     *
     * @param <F> тип фрагмента
     * @param cls класс фрагмента
     * @return content фрагмент
     */
    <F> F getContentFragment(Class<F> cls);

}
