package com.cleanarchitecture.shishkin.base.presenter;

import android.support.annotation.IdRes;
import android.view.View;

import com.cleanarchitecture.shishkin.base.controller.ISubscriber;

import java.util.ArrayList;

public interface IActivityPresenter extends ISubscriber {

    /**
     * Find view v.
     *
     * @param <V> the type parameter
     * @param id  the id
     * @return the v
     */
    <V extends View> V findView(@IdRes final int id);

    /**
     * Контроллировать права приложения
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
     * Show message.
     *
     * @param message the message
     */
    void showMessage(String message);

    /**
     * Show message.
     *
     * @param message the message
     * @param duration the duration
     */
    void showMessage(String message, int duration);

    /**
     * Show message.
     *
     * @param message the message
     * @param duration the duration
     * @param action the action text
     */
    void showMessage(String message, int duration, String action);

    /**
     * Hide keyboard.
     */
    void hideKeyboard();

    /**
     * Show keyboard.
     */
    void showKeyboard();

    /**
     * Lock orientation.
     */
    void lockOrientation();

    /**
     * Unlock orientation.
     */
    void unlockOrientation();

    /**
     * Show dialog.
     *
     * @param id      the id
     * @param title   the title
     * @param message the message
     */
    void showDialog(int id, int title, String message);

    /**
     * Show dialog.
     *
     * @param id              the id
     * @param title           the title
     * @param message         the message
     * @param button_positive the button positive
     */
    void showDialog(int id, int title, String message, int button_positive);

    /**
     * Show dialog.
     *
     * @param id              the id
     * @param title           the title
     * @param message         the message
     * @param button_positive the button positive
     * @param button_negative the button negative
     */
    void showDialog(int id, int title, String message, int button_positive, int button_negative);

    /**
     * Show dialog.
     *
     * @param id              the id
     * @param title           the title
     * @param message         the message
     * @param button_positive the button positive
     * @param button_negative the button negative
     * @param setCancelable   the set cancelable
     */
    void showDialog(int id, int title, String message, int button_positive, int button_negative, boolean setCancelable);

    /**
     * Show edit dialog.
     *
     * @param id              the id
     * @param title           the title
     * @param message         the message
     * @param editText        the edit text
     * @param hint            the hint
     * @param input_type      the input type
     * @param button_positive the button positive
     * @param button_negative the button negative
     * @param setCancelable   the set cancelable
     */
    void showEditDialog(int id, int title, String message, String editText, String hint, int input_type, int button_positive, int button_negative, boolean setCancelable);

    /**
     * Show list dialog.
     *
     * @param id              the id
     * @param title           the title
     * @param message         the message
     * @param list            the list
     * @param listSelected    the list selected
     * @param multiselect     the multiselect
     * @param button_positive the button positive
     * @param button_negative the button negative
     * @param setCancelable   the set cancelable
     */
    void showListDialog(int id, int title, String message, ArrayList<String> list, Integer[] listSelected, boolean multiselect, int button_positive, int button_negative, boolean setCancelable);

    /**
     * Show toast message.
     *
     * @param message the message
     */
    void showToast(String message);

    /**
     * Show toast message.
     *
     * @param message the message
     * @param duration the show duration
     */
    void showToast(String message, int duration);

    /**
     * Show toast message.
     *
     * @param message the message
     * @param duration the show duration
     * @param type the toast type
     */
    void showToast(String message, int duration, int type);

    void setStatusBarColor(final int color);
}
