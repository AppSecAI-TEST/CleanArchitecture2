package com.cleanarchitecture.shishkin.api.repository;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;

import com.cleanarchitecture.shishkin.api.controller.AbstractModule;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.EventBusController;
import com.cleanarchitecture.shishkin.api.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.api.data.Result;
import com.cleanarchitecture.shishkin.api.event.IEvent;
import com.cleanarchitecture.shishkin.api.event.repository.RepositoryTerminateRequestEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseRequestPermissionEvent;
import com.cleanarchitecture.shishkin.application.data.cursor.PhoneContactCursor;
import com.cleanarchitecture.shishkin.application.data.dao.PhoneContactDAO;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetChangedContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetChangedContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetDeletedContactsEvent;
import com.cleanarchitecture.shishkin.common.content.dao.AbstractReadOnlyDAO;
import com.cleanarchitecture.shishkin.common.utils.CloseUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

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

    private synchronized void requestCursorContacts(final RepositoryRequestGetContactsEvent event) {
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
                                    rows = Integer.valueOf(event.getRows() * 2 / 3);
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

    private synchronized void requestGetChangedContacts(final RepositoryRequestGetChangedContactsEvent event) {
        if (AdminUtils.checkPermission(Manifest.permission.READ_CONTACTS)) {
            final Context context = AdminUtils.getContext();
            if (context != null) {
                final Result<List<PhoneContactItem>> result = new PhoneContactDAO(context).getItems(context);
                if (!result.hasError() && result.getResult().size() > 0) {
                    final List<PhoneContactItem> changedList = new ArrayList<>();
                    final List<PhoneContactItem> contacts = event.getContacts();
                    for (PhoneContactItem item : result.getResult()) {
                        int pos = contacts.indexOf(item);
                        if (pos == -1) {
                            changedList.add(item);
                        } else {
                            if (event.getContacts().get(pos).hashCode() != item.hashCode()) {
                                changedList.add(item);
                            }
                        }
                    }
                    if (!changedList.isEmpty()) {
                        AdminUtils.postEvent(new RepositoryResponseGetChangedContactsEvent().setResponse(new Result<List<PhoneContactItem>>().setResult(changedList)));
                    }

                    final List<PhoneContactItem> deletedList = new ArrayList<>();
                    for (PhoneContactItem item : contacts) {
                        if (result.getResult().indexOf(item) == -1) {
                            deletedList.add(item);
                        }
                    }
                    if (!deletedList.isEmpty()) {
                        AdminUtils.postEvent(new RepositoryResponseGetDeletedContactsEvent().setResponse(new Result<List<PhoneContactItem>>().setResult(deletedList)));
                    }
                }
            }
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<String> getSubscription() {
        return StringUtils.arrayToList(EventBusController.NAME);
    }

    @Override
    public String getDescription() {
        return "Content Provider Module";
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRepositoryRequestCursorGetContactsEvent(final RepositoryRequestGetContactsEvent event) {
        requestCursorContacts(event);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRepositoryRequestGetChangedContactsEvent(final RepositoryRequestGetChangedContactsEvent event) {
        requestGetChangedContacts(event);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRepositoryTerminateRequestEvent(final RepositoryTerminateRequestEvent event) {
        if (mEvents.containsKey(event.getId())) {
            mTerminated.put(event.getId(), true);
        }
    }
}
