package com.cleanarchitecture.shishkin.api.event.toolbar;

import android.view.View;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

public class OnToolbarClickEvent extends AbstractEvent {

    private View mView;

    public OnToolbarClickEvent(final View view) {
        mView = view;
    }

    public View getView() {
        return mView;
    }

}
