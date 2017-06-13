package com.cleanarchitecture.shishkin.api.repository;

import com.cleanarchitecture.shishkin.api.controller.IModule;
import com.cleanarchitecture.shishkin.api.event.IEvent;

public interface IContentProvider extends IModule {

    IEvent getContacts();
}
