package com.cleanarchitecture.shishkin.api.controller;

import com.cleanarchitecture.shishkin.api.event.ui.HideKeyboardEvent;
import com.cleanarchitecture.shishkin.api.event.ui.HideProgressBarEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowDialogEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowEditDialogEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowErrorMessageEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowKeyboardEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowListDialogEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowMessageEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowProgressBarEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowToastEvent;
import com.cleanarchitecture.shishkin.api.ui.activity.IActivity;

/**
 * Интерфейс контроллера Activity
 */
@SuppressWarnings("unused")
public interface IActivityController extends IController<IActivity> {

    /**
     * Контроллировать право приложения
     *
     * @param permission право приложения
     * @return the boolean флаг - право приложению предоставлено
     */
    boolean checkPermission(String permission);

    /**
     * Запросить предоставление права приложению
     *
     * @param permission  право приложения
     * @param helpMessage сообщение, выводимое в диалоге предоставления права
     */
    void grantPermission(String permission, String helpMessage);

    /**
     * Контролировать наличие и текущую версию Google Play Services
     *
     * @return the boolean
     */
    boolean checkGooglePlayServices();

    /**
     * Обрабатывает событие - показать сообщение на экран
     *
     * @param event событие
     */
    void onShowMessageEvent(ShowMessageEvent event);

    /**
     * Обрабатывает событие - показать диалог об ошибке на экран
     *
     * @param event событие
     */
    void onShowErrorMessageEvent(ShowErrorMessageEvent event);

    /**
     * Обрабатывает событие - показать Toast на экран
     *
     * @param event событие
     */
    void onShowToastEvent(ShowToastEvent event);

    /**
     * Обрабатывает событие - скрыть клавиатуру
     *
     * @param event событие
     */
    void onHideKeyboardEvent(HideKeyboardEvent event);

    /**
     * Обрабатывает событие - показать клавиатуру
     *
     * @param event событие
     */
    void onShowKeyboardEvent(ShowKeyboardEvent event);

    /**
     * Обрабатывает событие - показать Progress Bar
     *
     * @param event событие
     */
    void onShowProgressBarEvent(ShowProgressBarEvent event);

    /**
     * Обрабатывает событие - скрыть Progress Bar
     *
     * @param event событие
     */
    void onHideProgressBarEvent(HideProgressBarEvent event);

    /**
     * Обрабатывает событие - показать диалок с выбором из списка
     *
     * @param event событие
     */
    void onShowListDialogEvent(ShowListDialogEvent event);

    /**
     * Обрабатывает событие - показать диалок с редактированием параметра
     *
     * @param event событие
     */
    void onShowEditDialogEvent(ShowEditDialogEvent event);

    /**
     * Обрабатывает событие - показать диалок
     *
     * @param event событие
     */
    void onShowDialogEvent(ShowDialogEvent event);
}
