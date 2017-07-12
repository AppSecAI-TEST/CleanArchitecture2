package com.cleanarchitecture.shishkin.api.presenter;

import android.graphics.drawable.Drawable;
import android.text.SpannableString;

import com.cleanarchitecture.shishkin.api.event.OnNetworkConnectedEvent;
import com.cleanarchitecture.shishkin.api.event.OnNetworkDisconnectedEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.OnToolbarClickEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarHideProgressBarEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetBackNavigationEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetBackgroundEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetBadgeEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetItemEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetMenuEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetTitleEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarShowProgressBarEvent;
import com.cleanarchitecture.shishkin.api.event.ui.HideHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowHorizontalProgressBarEvent;

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
     * Установить Badge
     *
     * @param count     отображаемое количество
     * @param isVisible the is visible
     */
    void setBadge(final int count, final boolean isVisible);

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

    /**
     * Установить фон Toolbar
     *
     * @param drawable фон
     */
    void setBackground(final Drawable drawable);

    /**
     * Обрабатывает событие - установить флаг наличия Back Navigation (кнопки Назад)
     *
     * @param event событие
     */
    void onSetToolbarBackNavigationEvent(ToolbarSetBackNavigationEvent event);

    /**
     * Обрабатывает событие - установить заголовок Toolbar
     *
     * @param event событие
     */
    void onSetToolbarTitleEvent(ToolbarSetTitleEvent event);

    /**
     * Обрабатывает событие - установить меню Toolbar
     *
     * @param event событие
     */
    void onSetToolbarMenuEvent(ToolbarSetMenuEvent event);

    /**
     * Обрабатывает событие - установить item Toolbar
     *
     * @param event событие
     */
    void onSetToolbarItemEvent(ToolbarSetItemEvent event);

    /**
     * Обрабатывает событие - показать горизонтальный Progress Bar
     *
     * @param event событие
     */
    void onShowHorizontalProgressBarEvent(ShowHorizontalProgressBarEvent event);

    /**
     * Обрабатывает событие - скрыть горизонтальный Progress Bar
     *
     * @param event событие
     */
    void onHideHorizontalProgressBarEvent(HideHorizontalProgressBarEvent event);

    /**
     * Обрабатывает событие - клик на Toolbar
     *
     * @param event событие
     */
    void onToolbarOnClickEvent(OnToolbarClickEvent event);

    /**
     * Обрабатывает событие - установить фон Toolbar
     *
     * @param event событие
     */
    void onToolbarSetBackgroundEvent(ToolbarSetBackgroundEvent event);

    /**
     * Обрабатывает событие - появилось соединение сети
     *
     * @param event событие
     */
    void onNetworkConnectedEvent(OnNetworkConnectedEvent event);

    /**
     * Обрабатывает событие - отсутствует соединение сети
     *
     * @param event событие
     */
    void onNetworkDisconnectedEvent(OnNetworkDisconnectedEvent event);

    /**
     * Обрабатывает событие - показать Progress Bar
     *
     * @param event событие
     */
    void onToolbarShowProgressBarEvent(ToolbarShowProgressBarEvent event);

    /**
     * Обрабатывает событие - скрыть Progress Bar
     *
     * @param event событие
     */
    void onToolbarHideProgressBarEvent(ToolbarHideProgressBarEvent event);

    /**
     * Обрабатывает событие - показать Badge
     *
     * @param event событие
     */
    void onToolbarSetBadgeEvent(ToolbarSetBadgeEvent event);
}