package com.cleanarchitecture.shishkin.base.presenter;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

public interface IContentFragmentPresenter {

    SwipeRefreshLayout getSwipeRefreshLayout();

    void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout);

    void onClick(View view);

}
