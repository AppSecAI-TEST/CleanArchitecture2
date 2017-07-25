package com.cleanarchitecture.shishkin.application.event.repository;

import com.cleanarchitecture.shishkin.api.event.repository.RepositoryResponseEvent;
import com.cleanarchitecture.shishkin.application.Constant;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;

import java.util.List;

public class RepositoryResponseGetChangedContactsEvent extends RepositoryResponseEvent<List<PhoneContactItem>> {

    public RepositoryResponseGetChangedContactsEvent() {
        super(Constant.REPOSITORY_GET_CHANGED_CONTACTS_EVENT);
    }
}
