package com.cleanarchitecture.shishkin.api.ui.recyclerview.event;

import android.support.v7.widget.RecyclerView;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

public class OnRecyclerViewIdleEvent extends AbstractEvent {
    private RecyclerView mRecyclerView;

    public OnRecyclerViewIdleEvent(final RecyclerView view) {
        mRecyclerView = view;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }
}
