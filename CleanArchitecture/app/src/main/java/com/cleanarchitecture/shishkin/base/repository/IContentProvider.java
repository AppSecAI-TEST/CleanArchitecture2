package com.cleanarchitecture.shishkin.base.repository;

import com.cleanarchitecture.shishkin.base.controller.IModule;
import com.cleanarchitecture.shishkin.base.event.IEvent;

public interface IContentProvider extends IModule {

    IEvent getContacts();
}
