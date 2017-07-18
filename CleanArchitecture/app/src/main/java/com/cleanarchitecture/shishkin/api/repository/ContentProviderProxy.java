package com.cleanarchitecture.shishkin.api.repository;

import android.Manifest;

import com.cleanarchitecture.shishkin.api.controller.AbstractModule;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.EventBusController;
import com.cleanarchitecture.shishkin.api.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseRequestPermissionEvent;
import com.cleanarchitecture.shishkin.api.storage.CacheUtils;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetContactsEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ContentProviderProxy extends AbstractModule implements IModuleSubscriber {

    public static final String NAME = ContentProviderProxy.class.getName();

    private void requestContacts(final RepositoryRequestGetContactsEvent event) {
        final List<PhoneContactItem> list = CacheUtils.getList(String.valueOf(event.getId()), event.getCacheType(), PhoneContactItem.class);
        if (list != null) {
            AdminUtils.postEvent(new RepositoryResponseGetContactsEvent()
                    .setResponse(list)
                    .setFrom(Repository.FROM_CACHE));
        } else {
            if (AdminUtils.checkPermission(Manifest.permission.READ_CONTACTS)) {
                final IContentProvider contentProvider = AdminUtils.getContentProvider();
                if (contentProvider != null) {
                    final RepositoryResponseGetContactsEvent responseEvent = (RepositoryResponseGetContactsEvent) contentProvider.getContacts();
                    responseEvent.setFrom(Repository.FROM_CONTENT_PROVIDER);

                    if (!responseEvent.hasError()) {
                        CacheUtils.put(String.valueOf(event.getId()), event.getCacheType(), responseEvent.getResponse(), event.getExpired());
                    }

                    AdminUtils.postEvent(responseEvent);
                }
            } else {
                AdminUtils.postEvent(new UseCaseRequestPermissionEvent(Manifest.permission.READ_CONTACTS));
            }
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return null;
    }

    @Override
    public List<String> hasSubscriberType() {
        final ArrayList<String> list = new ArrayList<>();
        list.add(EventBusController.SUBSCRIBER_TYPE);
        return list;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRepositoryRequestGetContactsEvent(final RepositoryRequestGetContactsEvent event) {
        requestContacts(event);
    }

}
