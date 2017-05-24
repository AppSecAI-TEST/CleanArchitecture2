package com.cleanarchitecture.shishkin.base.presenter;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

public interface IContentFragmentPresenter {

    /**
     * Получить SwipeRefreshLayout презентера
     *
     * @return the swipe refresh layout
     */
    SwipeRefreshLayout getSwipeRefreshLayout();

    /**
     * Установить SwipeRefreshLayout презентера
     *
     * @param swipeRefreshLayout the swipe refresh layout
     */
    void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout);

    /**
     * Событие - onClick
     *
     * @param view the view
     */
    void onClick(View view);

}
