package com.cleanarchitecture.shishkin.api.repository;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;

import com.cleanarchitecture.shishkin.api.controller.AbstractModule;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.EventBusController;
import com.cleanarchitecture.shishkin.api.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.api.event.IEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseRequestPermissionEvent;
import com.cleanarchitecture.shishkin.api.storage.CacheUtils;
import com.cleanarchitecture.shishkin.application.data.cursor.PhoneContactCursor;
import com.cleanarchitecture.shishkin.application.data.dao.PhoneContactDAO;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestCursorGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetContactsEvent;
import com.cleanarchitecture.shishkin.common.content.dao.AbstractReadOnlyDAO;
import com.cleanarchitecture.shishkin.common.utils.CloseUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContentProviderProxy extends AbstractModule implements IModuleSubscriber {

    public static final String NAME = ContentProviderProxy.class.getName();

    private Map<Integer, IEvent> mEvents = Collections.synchronizedMap(new ConcurrentHashMap<Integer, IEvent>());

    private synchronized void requestContacts(final RepositoryRequestGetContactsEvent event) {
        if (!mEvents.containsKey(event.getId())) {
            mEvents.put(event.getId(), event);

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

            mEvents.remove(event.getId());
        }
    }

    private synchronized void requestCursorContacts(final RepositoryRequestCursorGetContactsEvent event) {
        if (!mEvents.containsKey(event.getId())) {
            mEvents.put(event.getId(), event);

            final Context context = AdminUtils.getContext();
            if (context != null) {
                final Cursor cursor = PhoneContactCursor.getCursor(context);
                if (AbstractReadOnlyDAO.isCursorValid(cursor)) {
                    RepositoryResponseGetContactsEvent responseEvent;
                    while (true) {
                        responseEvent = new RepositoryResponseGetContactsEvent();
                        responseEvent.setResponse(new PhoneContactDAO(context).getItems(context, cursor, event.getRows()));
                        AdminUtils.postEvent(responseEvent);
                        if (cursor.isAfterLast() || responseEvent.hasError()) {
                            CloseUtils.close(cursor);
                            break;
                        }
                    }
                }
            }
            mEvents.remove(event.getId());
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

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRepositoryRequestCursorGetContactsEvent(final RepositoryRequestCursorGetContactsEvent event) {
        requestCursorContacts(event);
    }

}
