package com.cleanarchitecture.shishkin.api.ui.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

public interface IContentFragment extends IFragment {
    /**
     * Получить SwipeRefreshLayout презентера
     *
     * @return the swipe refresh layout
     */
    SwipeRefreshLayout getSwipeRefreshLayout();

    /**
     * Событие - onClick
     *
     * @param view the view
     */
    void onClick(View view);

    /**
     * Обновить данные во фрагменте
     */
    void refreshData();

    /**
     * Обновить views во фрагменте
     */
    void refreshViews();

}
