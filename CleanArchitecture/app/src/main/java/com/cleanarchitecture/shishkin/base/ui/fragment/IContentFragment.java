package com.cleanarchitecture.shishkin.base.ui.fragment;

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
}
