package com.cleanarchitecture.shishkin.api.repository;

import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetContactsEvent;
import com.cleanarchitecture.shishkin.common.utils.SerializableUtil;

import java.io.Serializable;
import java.util.List;

public class RepositoryContentProvider {
    private RepositoryContentProvider() {
    }

    public static synchronized void requestContacts(final RepositoryRequestGetContactsEvent event) {
        final IRepository repository = AdminUtils.getRepository();
        if (repository != null) {
            final List<PhoneContactItem> list = SerializableUtil.serializableToList(repository.getFromCache(String.valueOf(event.getId()), event.getCacheType()));
            if (list != null) {
                AdminUtils.postEvent(new RepositoryResponseGetContactsEvent()
                        .setResponse(list)
                        .setFrom(Repository.FROM_CACHE));
            } else {
                final RepositoryResponseGetContactsEvent responseEvent = (RepositoryResponseGetContactsEvent) AdminUtils.getContentProvider().getContacts();
                responseEvent.setFrom(Repository.FROM_CONTENT_PROVIDER);

                if (!responseEvent.hasError()) {
                    repository.putToCache(String.valueOf(event.getId()), event.getCacheType(), (Serializable) responseEvent.getResponse(), event.getExpired());
                }

                AdminUtils.postEvent(responseEvent);
            }
        }
    }
}
