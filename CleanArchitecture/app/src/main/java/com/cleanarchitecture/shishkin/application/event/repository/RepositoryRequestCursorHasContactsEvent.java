package com.cleanarchitecture.shishkin.application.event.repository;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.event.AbstractEvent;
import com.cleanarchitecture.shishkin.application.Constant;

public class RepositoryRequestCursorHasContactsEvent extends AbstractEvent {

    private int mRows = 50;

    public RepositoryRequestCursorHasContactsEvent(int rows) {
        super(Constant.REPOSITORY_REQUEST_CURSOR_GET_CONTACTS_EVENT);

        mRows = rows;
    }

    public int getRows() {
        return mRows;
    }

}
