package com.cleanarchitecture.shishkin.base.repository;

import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetContactsEvent;
import com.cleanarchitecture.shishkin.base.controller.ISubscriber;

public interface IContentProvider extends ISubscriber {

    RepositoryResponseGetContactsEvent getContacts();
}
