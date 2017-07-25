package com.cleanarchitecture.shishkin.application.event.repository;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;
import com.cleanarchitecture.shishkin.application.Constant;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;

import java.util.List;

public class RepositoryRequestGetChangedContactsEvent extends AbstractEvent {

    private List<PhoneContactItem> mContacts;

    public RepositoryRequestGetChangedContactsEvent(final List<PhoneContactItem> contacts) {
        super(Constant.REPOSITORY_GET_CHANGED_CONTACTS_EVENT);

        mContacts = contacts;
    }

    public List<PhoneContactItem> getContacts() {
        return mContacts;
    }
}
