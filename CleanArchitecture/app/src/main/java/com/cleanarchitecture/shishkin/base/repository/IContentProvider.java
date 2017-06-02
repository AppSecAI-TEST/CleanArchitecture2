package com.cleanarchitecture.shishkin.base.repository;

import com.cleanarchitecture.shishkin.base.controller.ISubscriber;
import com.cleanarchitecture.shishkin.base.event.IEvent;

public interface IContentProvider extends ISubscriber {

    IEvent getContacts();
}
