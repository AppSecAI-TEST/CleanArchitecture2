package com.cleanarchitecture.shishkin.base.repository;

import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetContactsEvent;
import com.cleanarchitecture.shishkin.base.utils.AdminUtils;
import com.cleanarchitecture.shishkin.base.utils.SerializableUtil;

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
                final RepositoryResponseGetContactsEvent responseEvent = (RepositoryResponseGetContactsEvent) repository.getContentProvider().getContacts();
                responseEvent.setFrom(Repository.FROM_CONTENT_PROVIDER);

                if (!responseEvent.hasError()) {
                    repository.putToCache(String.valueOf(event.getId()), event.getCacheType(), (Serializable) responseEvent.getResponse());
                }

                AdminUtils.postEvent(responseEvent);
            }
        }
    }


}
