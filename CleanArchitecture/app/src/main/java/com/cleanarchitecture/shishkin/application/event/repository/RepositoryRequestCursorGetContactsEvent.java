package com.cleanarchitecture.shishkin.application.event.repository;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;
import com.cleanarchitecture.shishkin.application.Constant;

public class RepositoryRequestCursorGetContactsEvent extends AbstractEvent {

    private int mRows = 50;

    public RepositoryRequestCursorGetContactsEvent(int rows) {
        super(Constant.REPOSITORY_REQUEST_CURSOR_GET_CONTACTS_EVENT);

        mRows = rows;
    }

    public int getRows() {
        return mRows;
    }

}
