package com.cleanarchitecture.shishkin.application.event.repository;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.base.event.AbstractEvent;

import java.util.List;

public class RepositoryResponseGetContactsEvent extends AbstractEvent {

    private List<PhoneContactItem> mList;

    private int mFrom = -1;

    public RepositoryResponseGetContactsEvent(final List<PhoneContactItem> list, final int from) {
        setId(R.id.repository_get_contacts);

        mList = list;
        mFrom = from;
    }

    public List<PhoneContactItem> getContacts() {
        return mList;
    }

    public int getFrom() {
        return mFrom;
    }

}
