package com.cleanarchitecture.shishkin.api.repository;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;

import com.cleanarchitecture.shishkin.api.controller.AbstractModule;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.EventBusController;
import com.cleanarchitecture.shishkin.api.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.api.event.IEvent;
import com.cleanarchitecture.shishkin.api.event.repository.RepositoryTerminateRequestEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseRequestPermissionEvent;
import com.cleanarchitecture.shishkin.application.data.cursor.PhoneContactCursor;
import com.cleanarchitecture.shishkin.application.data.dao.PhoneContactDAO;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestCursorGetContactsEvent;
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

public class ContentProvider extends AbstractModule implements IModuleSubscriber {

    public static final String NAME = ContentProvider.class.getName();

    private Map<Integer, IEvent> mEvents = Collections.synchronizedMap(new ConcurrentHashMap<Integer, IEvent>());
    private Map<Integer, Boolean> mTerminated = Collections.synchronizedMap(new ConcurrentHashMap<Integer, Boolean>());

    private synchronized void requestCursorContacts(final RepositoryRequestCursorGetContactsEvent event) {
        if (AdminUtils.checkPermission(Manifest.permission.READ_CONTACTS)) {
            if (!mEvents.containsKey(event.getId())) {
                mEvents.put(event.getId(), event);

                final Context context = AdminUtils.getContext();
                if (context != null) {
                    final Cursor cursor = PhoneContactCursor.getCursor(context);
                    if (AbstractReadOnlyDAO.isCursorValid(cursor)) {
                        RepositoryResponseGetContactsEvent responseEvent;
                        int i = 0;
                        while (true) {
                            responseEvent = new RepositoryResponseGetContactsEvent();
                            if (!mTerminated.containsKey(event.getId())) {
                                int rows;
                                if (i == 0) {
                                    rows = event.getRows() / 2;
                                } else if (i == 1) {
                                    rows = event.getRows() * 2 / 3;
                                } else {
                                    rows = event.getRows();
                                }
                                responseEvent.setResponse(new PhoneContactDAO(context).getItems(context, cursor, rows));
                                AdminUtils.postEvent(responseEvent);
                                if (cursor.isAfterLast() || responseEvent.hasError()) {
                                    break;
                                }
                                i++;
                            } else {
                                AdminUtils.postEvent(responseEvent);
                                break;
                            }
                        }
                    }
                    CloseUtils.close(cursor);
                }
                mTerminated.remove(event.getId());
                mEvents.remove(event.getId());
            }
        } else {
            AdminUtils.postEvent(new UseCaseRequestPermissionEvent(Manifest.permission.READ_CONTACTS));
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
    public void onRepositoryRequestCursorGetContactsEvent(final RepositoryRequestCursorGetContactsEvent event) {
        requestCursorContacts(event);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRepositoryTerminateRequestEvent(final RepositoryTerminateRequestEvent event) {
        if (mEvents.containsKey(event.getId())) {
            mTerminated.put(event.getId(), true);
        }
    }
}
