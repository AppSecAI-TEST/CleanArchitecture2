package com.cleanarchitecture.shishkin.application.event.repository;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.base.event.repository.RepositoryResponseEvent;

import java.util.List;

public class RepositoryResponseGetContactsEvent extends RepositoryResponseEvent<List<PhoneContactItem>> {

    public RepositoryResponseGetContactsEvent() {
        setId(R.id.repository_get_contacts);
    }

}
