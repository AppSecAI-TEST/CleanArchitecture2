package com.cleanarchitecture.shishkin.api.ui.recyclerview;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.ui.recyclerview.event.OnRecyclerViewIdleEvent;
import com.cleanarchitecture.shishkin.api.ui.recyclerview.event.OnRecyclerViewScrolledEvent;

public class OnScrollListener extends RecyclerView.OnScrollListener {

    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public OnScrollListener(final RecyclerView recyclerView, final SwipeRefreshLayout swipeRefreshLayout) {
        mRecyclerView = recyclerView;
        mLinearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        mSwipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (recyclerView == null) {
            return;
        }

        if (mSwipeRefreshLayout != null) {
            if (recyclerView.getAdapter().getItemCount() == 0) {
                mSwipeRefreshLayout.setEnabled(true);
            } else {
                mSwipeRefreshLayout.setEnabled(mLinearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            }
        }

        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            AdminUtils.postEvent(new OnRecyclerViewIdleEvent(recyclerView));
        } else {
            if (!(mLinearLayoutManager.findLastCompletelyVisibleItemPosition() == mRecyclerView.getAdapter().getItemCount() - 1)) {
                AdminUtils.postEvent(new OnRecyclerViewScrolledEvent(recyclerView));
            }
        }
    }

}
