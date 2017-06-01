package com.cleanarchitecture.shishkin.base.repository;

import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetContactsEvent;
import com.cleanarchitecture.shishkin.base.controller.Controllers;
import com.cleanarchitecture.shishkin.base.controller.EventBusController;
import com.cleanarchitecture.shishkin.base.utils.SerializableUtil;

import java.io.Serializable;
import java.util.List;

public class RepositoryContentProvider {
    private RepositoryContentProvider() {
    }

    public static synchronized void requestContacts(final RepositoryRequestGetContactsEvent event) {
        final List<PhoneContactItem> list = SerializableUtil.serializableToList(Controllers.getInstance().getRepository().getFromCache(String.valueOf(event.getId()), event.getCacheType()));
        if (list != null) {
            EventBusController.getInstance().post(new RepositoryResponseGetContactsEvent()
                            .setResponse(list)
                            .setFrom(Repository.FROM_CACHE));
        } else {
            final RepositoryResponseGetContactsEvent responseEvent = (RepositoryResponseGetContactsEvent)Controllers.getInstance().getRepository().getContentProvider().getContacts();
            responseEvent.setFrom(Repository.FROM_CONTENT_PROVIDER);

            if (responseEvent.getResponse() != null && !responseEvent.hasError()) {
                Controllers.getInstance().getRepository().putToCache(String.valueOf(event.getId()), event.getCacheType(), (Serializable) responseEvent.getResponse());
            }
            EventBusController.getInstance().post(responseEvent);
        }
    }


}
