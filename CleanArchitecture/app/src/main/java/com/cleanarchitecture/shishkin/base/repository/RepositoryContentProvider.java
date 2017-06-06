package com.cleanarchitecture.shishkin.base.repository;

import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetContactsEvent;
import com.cleanarchitecture.shishkin.base.controller.Admin;
import com.cleanarchitecture.shishkin.base.controller.EventBusController;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.base.utils.SerializableUtil;

import java.io.Serializable;
import java.util.List;

public class RepositoryContentProvider {
    private RepositoryContentProvider() {
    }

    public static synchronized void requestContacts(final RepositoryRequestGetContactsEvent event) {
        final IRepository repository = ApplicationUtils.getRepository();
        if (repository != null) {
            final List<PhoneContactItem> list = SerializableUtil.serializableToList(repository.getFromCache(String.valueOf(event.getId()), event.getCacheType()));
            if (list != null) {
                ApplicationUtils.postEvent(new RepositoryResponseGetContactsEvent()
                        .setResponse(list)
                        .setFrom(Repository.FROM_CACHE));
            } else {
                final RepositoryResponseGetContactsEvent responseEvent = (RepositoryResponseGetContactsEvent) repository.getContentProvider().getContacts();
                responseEvent.setFrom(Repository.FROM_CONTENT_PROVIDER);

                if (!responseEvent.hasError()) {
                    repository.putToCache(String.valueOf(event.getId()), event.getCacheType(), (Serializable) responseEvent.getResponse());
                }

                ApplicationUtils.postEvent(responseEvent);
            }
        }
    }


}
