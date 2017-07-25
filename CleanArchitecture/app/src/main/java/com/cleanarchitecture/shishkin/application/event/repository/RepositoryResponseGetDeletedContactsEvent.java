package com.cleanarchitecture.shishkin.application.event.repository;

import com.cleanarchitecture.shishkin.api.event.repository.RepositoryResponseEvent;
import com.cleanarchitecture.shishkin.application.Constant;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;

import java.util.List;

public class RepositoryResponseGetDeletedContactsEvent extends RepositoryResponseEvent<List<PhoneContactItem>> {

    public RepositoryResponseGetDeletedContactsEvent() {
        super(Constant.REPOSITORY_GET_DELETED_CONTACTS_EVENT);
    }
}
