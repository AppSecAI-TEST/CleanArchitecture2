package com.cleanarchitecture.shishkin.base.ui.recyclerview.event;

import android.support.v7.widget.RecyclerView;

import com.cleanarchitecture.shishkin.base.event.AbstractEvent;

public class OnRecyclerViewScrolledEvent extends AbstractEvent {
    private RecyclerView mRecyclerView;

    public OnRecyclerViewScrolledEvent(final RecyclerView view) {
        mRecyclerView = view;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }
}
