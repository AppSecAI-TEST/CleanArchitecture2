package com.cleanarchitecture.shishkin.base.presenter;

public interface IToolbarPresenter {

    boolean hasBackNavigation();

    void setBackNavigation(boolean backNavigation);

    void hideHorizontalProgressBar();

    void showHorizontalProgressBar();

    void resetToolbar();

    void setHome(int iconId, boolean isVisible);

    boolean isShow();

    void setShow(boolean isShow);

    void setItem(int itemId, boolean isVisible);

    void setMenu(int menuId, boolean isVisible);

    void setTitle(int iconId, String title);
}
