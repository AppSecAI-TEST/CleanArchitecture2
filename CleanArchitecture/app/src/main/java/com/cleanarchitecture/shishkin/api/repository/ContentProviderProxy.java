package com.cleanarchitecture.shishkin.api.repository;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;

import com.cleanarchitecture.shishkin.api.controller.AbstractModule;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.EventBusController;
import com.cleanarchitecture.shishkin.api.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.api.event.IEvent;
import com.cleanarchitecture.shishkin.api.event.repository.RemoveCursorEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseRequestPermissionEvent;
import com.cleanarchitecture.shishkin.api.storage.CacheUtils;
import com.cleanarchitecture.shishkin.application.data.cursor.PhoneContactCursor;
import com.cleanarchitecture.shishkin.application.data.dao.PhoneContactDAO;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestCursorGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestCursorHasContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseCursorGetContactsEvent;
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
    private Map<Integer, Cursor> mCursors = Collections.synchronizedMap(new ConcurrentHashMap<Integer, Cursor>());

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
                removeCursor(event.getId());

                final Cursor cursor = PhoneContactCursor.getCursor(context);
                if (AbstractReadOnlyDAO.isCursorValid(cursor)) {
                    mCursors.put(event.getId(), cursor);

                    final RepositoryResponseCursorGetContactsEvent responseEvent = new RepositoryResponseCursorGetContactsEvent();
                    responseEvent.setResponse(new PhoneContactDAO(context).getItems(context, cursor, event.getRows()));
                    if (cursor.isAfterLast()) {
                        removeCursor(event.getId());
                        responseEvent.setBOF(true);
                    }
                    AdminUtils.postEvent(responseEvent);
                }
            }
            mEvents.remove(event.getId());
        }
    }

    private synchronized void requestCursorContacts(final RepositoryRequestCursorHasContactsEvent event) {
        if (!mEvents.containsKey(event.getId())) {
            mEvents.put(event.getId(), event);

            final Context context = AdminUtils.getContext();
            if (context != null) {
                final Cursor cursor = mCursors.get(event.getId());
                if (AbstractReadOnlyDAO.isCursorValid(cursor)) {
                    final RepositoryResponseCursorGetContactsEvent responseEvent = new RepositoryResponseCursorGetContactsEvent();
                    responseEvent.setResponse(new PhoneContactDAO(context).getItems(context, cursor, event.getRows()));
                    if (cursor.isAfterLast()) {
                        removeCursor(event.getId());
                        responseEvent.setBOF(true);
                    }
                    AdminUtils.postEvent(responseEvent);
                }
            }
            mEvents.remove(event.getId());
        }
    }

    private synchronized void removeCursor(int id) {
        if (mCursors.containsKey(id)) {
            final Cursor cursor = mCursors.get(id);
            if (cursor != null) {
                CloseUtils.close(cursor);
            }
            mCursors.remove(id);
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

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRepositoryRequestCursorHasContactsEvent(final RepositoryRequestCursorHasContactsEvent event) {
        requestCursorContacts(event);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRemoveCursorEvent(final RemoveCursorEvent event) {
        removeCursor(event.getId());
    }

}
