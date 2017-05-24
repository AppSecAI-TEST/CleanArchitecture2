package com.cleanarchitecture.shishkin.base.presenter;

public interface IToolbarPresenter {

    /**
     * Флаг - поддерживает Toolbar backpress навигацию
     *
     * @return the boolean
     */
    boolean hasBackNavigation();

    /**
     * Установить/сбросить поддержку Toolbar backpress навигации
     *
     * @param backNavigation the back navigation
     */
    void setBackNavigation(boolean backNavigation);

    /**
     * Скрыть горизонтальный progress bar.
     */
    void hideHorizontalProgressBar();

    /**
     * Показать горизонтальный progress bar.
     */
    void showHorizontalProgressBar();

    /**
     * Сбросить toolbar.
     */
    void resetToolbar();

    /**
     * Установить View кнопка Backpress
     *
     * @param iconId    the icon id
     * @param isVisible the is visible
     */
    void setHome(int iconId, boolean isVisible);

    /**
     * Флаг - показывать/скрыть Toolbar
     *
     * @return the boolean
     */
    boolean isShow();

    /**
     * Показывать/скрыть Toolbar
     *
     * @param isShow the is show
     */
    void setShow(boolean isShow);

    /**
     * Установить параметры Item Toolbar
     *
     * @param itemId    the item id
     * @param isVisible the is visible
     */
    void setItem(int itemId, boolean isVisible);

    /**
     * Установить параметры Menu Toolbar
     *
     * @param menuId    the menu id
     * @param isVisible the is visible
     */
    void setMenu(int menuId, boolean isVisible);

    /**
     * Установить заголовок Toolbar
     *
     * @param iconId the icon id
     * @param title  the title
     */
    void setTitle(int iconId, String title);
}
