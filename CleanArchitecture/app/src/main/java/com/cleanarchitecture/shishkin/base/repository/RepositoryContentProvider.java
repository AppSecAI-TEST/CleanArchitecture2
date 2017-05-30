package com.cleanarchitecture.shishkin.base.repository;

import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetContactsEvent;
import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.utils.SerializableUtil;

import java.io.Serializable;
import java.util.List;

public class RepositoryContentProvider {
    private RepositoryContentProvider() {
    }

    public static synchronized void requestContacts(final RepositoryRequestGetContactsEvent event) {
        List<PhoneContactItem> list = SerializableUtil.serializableToList(Repository.getInstance().getFromCache(String.valueOf(event.getId()), event.getCacheType()));
        if (list != null) {
            EventController.getInstance().post(new RepositoryResponseGetContactsEvent(list, Repository.FROM_CACHE));
        } else {
            list = ContentProvider.getInstance().getContacts();

            if (list != null) {
                Repository.getInstance().putToCache(String.valueOf(event.getId()), event.getCacheType(), (Serializable) list);
            }
            EventController.getInstance().post(new RepositoryResponseGetContactsEvent(list, Repository.FROM_CONTENT_PROVIDER));
        }
    }



}
