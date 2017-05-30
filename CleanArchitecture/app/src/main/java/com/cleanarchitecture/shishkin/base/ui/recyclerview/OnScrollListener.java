package com.cleanarchitecture.shishkin.base.ui.recyclerview;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.ui.recyclerview.event.OnRecyclerViewFirstRecordVisibledEvent;
import com.cleanarchitecture.shishkin.base.ui.recyclerview.event.OnRecyclerViewIdleEvent;
import com.cleanarchitecture.shishkin.base.ui.recyclerview.event.OnRecyclerViewLastRecordVisibledEvent;
import com.cleanarchitecture.shishkin.base.ui.recyclerview.event.OnRecyclerViewScrolledEvent;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class OnScrollListener extends RecyclerView.OnScrollListener {

    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public OnScrollListener(final RecyclerView recyclerView, final SwipeRefreshLayout swipeRefreshLayout) {
        mRecyclerView = recyclerView;
        mLinearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        mSwipeRefreshLayout = swipeRefreshLayout;

        checkPosition();
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        checkPosition();
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (mSwipeRefreshLayout != null) {
            if (recyclerView.getAdapter().getItemCount() == 0) {
                mSwipeRefreshLayout.setEnabled(true);
            } else {
                mSwipeRefreshLayout.setEnabled(mLinearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            }
        }
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            EventController.getInstance().post(new OnRecyclerViewIdleEvent(recyclerView));
            checkPosition();
        } else {
            if (!(mLinearLayoutManager.findLastCompletelyVisibleItemPosition() == mRecyclerView.getAdapter().getItemCount() - 1)) {
                EventController.getInstance().post(new OnRecyclerViewScrolledEvent(recyclerView));
            }
        }
    }

    private void checkPosition(){
        if (mLinearLayoutManager.findLastCompletelyVisibleItemPosition() == mRecyclerView.getAdapter().getItemCount() - 1){
            EventController.getInstance().post(new OnRecyclerViewLastRecordVisibledEvent(mRecyclerView, VISIBLE));
        } else {
            EventController.getInstance().post(new OnRecyclerViewLastRecordVisibledEvent(mRecyclerView, GONE));
        }

        if (mLinearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
            EventController.getInstance().post(new OnRecyclerViewFirstRecordVisibledEvent(mRecyclerView, VISIBLE));
        } else {
            EventController.getInstance().post(new OnRecyclerViewFirstRecordVisibledEvent(mRecyclerView, GONE));
        }
    }

}
