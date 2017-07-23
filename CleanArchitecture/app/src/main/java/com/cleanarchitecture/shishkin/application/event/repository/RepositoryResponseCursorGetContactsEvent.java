package com.cleanarchitecture.shishkin.application.event.repository;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.event.repository.RepositoryResponseEvent;
import com.cleanarchitecture.shishkin.application.Constant;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;

import java.util.List;

public class RepositoryResponseCursorGetContactsEvent extends RepositoryResponseEvent<List<PhoneContactItem>> {

    private boolean mBOF = false;

    public RepositoryResponseCursorGetContactsEvent() {
        super(Constant.REPOSITORY_REQUEST_CURSOR_GET_CONTACTS_EVENT);
    }

    public boolean isBOF() {
        return mBOF;
    }

    public void setBOF(boolean bof) {
        this.mBOF = bof;
    }

}
