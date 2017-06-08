package com.cleanarchitecture.shishkin.base.repository;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.application.app.Constant;
import com.cleanarchitecture.shishkin.application.data.cursor.PhoneContactCursor;
import com.cleanarchitecture.shishkin.application.data.dao.PhoneContactDAO;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetContactsEvent;
import com.cleanarchitecture.shishkin.base.content.dao.AbstractReadOnlyDAO;
import com.cleanarchitecture.shishkin.base.controller.ErrorController;
import com.cleanarchitecture.shishkin.base.event.IEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseRequestPermissionEvent;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.base.utils.CloseUtils;

import java.util.LinkedList;

public class ContentProvider implements IContentProvider {
    public static final String NAME = "ContentProvider";

    public ContentProvider() {
    }

    @NonNull
    public synchronized IEvent getContacts() {
        final RepositoryResponseGetContactsEvent event = (RepositoryResponseGetContactsEvent) new RepositoryResponseGetContactsEvent()
                .setId(Constant.REPOSITORY_GET_CONTACTS);

        final Context context = ApplicationController.getInstance();
        if (context == null) {
            return event.setErrorCode(NAME, ErrorController.ERROR_LOST_AAPLICATION_CONTEXT);
        }

        if (!ApplicationUtils.checkPermission(Manifest.permission.READ_CONTACTS)) {
            ApplicationUtils.postEvent(new UseCaseRequestPermissionEvent(Manifest.permission.READ_CONTACTS));
            return event.setErrorText(NAME, context.getString(R.string.permission_read_contacts));
        }

        final LinkedList<PhoneContactItem> list = new LinkedList<>();
        Cursor cursor = null;
        try {
            final PhoneContactDAO phoneContactDAO = new PhoneContactDAO(context);
            cursor = PhoneContactCursor.getCursor(context);
            if (AbstractReadOnlyDAO.isCursorValid(cursor)) {
                while (cursor.moveToNext()) {
                    final PhoneContactItem phoneContactItem = phoneContactDAO.getItemFromCursor(cursor);
                    list.add(phoneContactItem);
                }
            }
            event.setResponse(list);
        } catch (Exception e) {
            event.setErrorText(NAME, e, context.getString(R.string.error_read_phone_contacts));
        } finally {
            if (cursor != null) {
                CloseUtils.close(cursor);
            }
        }
        return event;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return null;
    }

}
