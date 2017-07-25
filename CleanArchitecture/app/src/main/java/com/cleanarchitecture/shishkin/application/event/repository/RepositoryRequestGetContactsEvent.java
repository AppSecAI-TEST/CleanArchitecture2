package com.cleanarchitecture.shishkin.application.event.repository;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;
import com.cleanarchitecture.shishkin.application.Constant;

public class RepositoryRequestGetContactsEvent extends AbstractEvent {

    private int mRows = 50;

    public RepositoryRequestGetContactsEvent(int rows) {
        super(Constant.REPOSITORY_GET_CONTACTS_EVENT);

        mRows = rows;
    }

    public int getRows() {
        return mRows;
    }

}
