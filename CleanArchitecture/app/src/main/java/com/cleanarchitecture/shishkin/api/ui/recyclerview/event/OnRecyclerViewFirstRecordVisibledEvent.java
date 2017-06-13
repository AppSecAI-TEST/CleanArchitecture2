package com.cleanarchitecture.shishkin.api.ui.recyclerview.event;

import android.support.v7.widget.RecyclerView;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

public class OnRecyclerViewFirstRecordVisibledEvent extends AbstractEvent {
    private RecyclerView mRecyclerView;
    private int mState = -100;

    public OnRecyclerViewFirstRecordVisibledEvent(final RecyclerView view, final int state) {
        mRecyclerView = view;
        mState = state;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }
}
