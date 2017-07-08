package com.cleanarchitecture.shishkin.api.repository;

import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.storage.CacheUtils;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetContactsEvent;

import java.util.List;

public class RepositoryContentProviderUtils {

    private RepositoryContentProviderUtils() {
    }

    public static void requestContacts(final RepositoryRequestGetContactsEvent event) {
        final IRepository repository = AdminUtils.getRepository();
        if (repository != null) {
            final List<PhoneContactItem> list = CacheUtils.getList(String.valueOf(event.getId()), event.getCacheType(), PhoneContactItem.class);
            if (list != null) {
                AdminUtils.postEvent(new RepositoryResponseGetContactsEvent()
                        .setResponse(list)
                        .setFrom(Repository.FROM_CACHE));
            } else {
                final RepositoryResponseGetContactsEvent responseEvent = (RepositoryResponseGetContactsEvent) AdminUtils.getContentProvider().getContacts();
                responseEvent.setFrom(Repository.FROM_CONTENT_PROVIDER);

                if (!responseEvent.hasError()) {
                    CacheUtils.put(String.valueOf(event.getId()), event.getCacheType(), (List<PhoneContactItem>) responseEvent.getResponse(), event.getExpired());
                }

                AdminUtils.postEvent(responseEvent);
            }
        }
    }
}
