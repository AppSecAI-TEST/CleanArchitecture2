package com.cleanarchitecture.shishkin.base.repository;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.application.data.cursor.ContactCursor;
import com.cleanarchitecture.shishkin.application.data.dao.PhoneContactDAO;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.database.CleanArchitectureDb;
import com.cleanarchitecture.shishkin.application.database.item.Contact;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetContactsEvent;
import com.cleanarchitecture.shishkin.base.controller.Controllers;
import com.cleanarchitecture.shishkin.base.controller.EventBusController;
import com.cleanarchitecture.shishkin.base.database.dao.AbstractReadOnlyDAO;
import com.cleanarchitecture.shishkin.base.event.IEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseRequestPermissionEvent;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.base.utils.CloseUtils;
import com.github.snowdream.android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class ContentProvider {
    public static final String NAME = "ContentProvider";

    public ContentProvider() {
    }

    public synchronized IEvent getContacts() {
        final RepositoryResponseGetContactsEvent event  = new RepositoryResponseGetContactsEvent();

        final Context context = ApplicationController.getInstance();
        if (context == null) {
            return event.setErrorCode(1);
        }

        if (!ApplicationUtils.checkPermission(Manifest.permission.READ_CONTACTS)) {
            EventBusController.getInstance().post(new UseCaseRequestPermissionEvent(Manifest.permission.READ_CONTACTS));
            return event.setErrorText(context.getString(R.string.permission_read_contacts));
        }

        final CleanArchitectureDb db = (CleanArchitectureDb)Controllers.getInstance().getRepository().getDbProvider().getDb();
        if (db == null) {
            return event.setErrorText(context.getString(R.string.error_db_not_connected));
        }

        final LinkedList<PhoneContactItem> list = new LinkedList<>();
        Cursor cursor = null;
        try {
            final PhoneContactDAO phoneContactDAO = new PhoneContactDAO(context);
            cursor = ContactCursor.getCursor(context);
            if (AbstractReadOnlyDAO.isCursorValid(cursor)) {
                while (cursor.moveToNext()) {
                    final PhoneContactItem phoneContactItem = phoneContactDAO.getItemFromCursor(cursor);
                    if (phoneContactItem != null) {
                        list.add(phoneContactItem);
                        if (db.contactDao().getCount(phoneContactItem.getId()) == 0) {
                            db.contactDao().insert(new Contact()
                                    .setRowId(phoneContactItem.getId())
                                    .setName(phoneContactItem.getName())
                                    .setPhones(phoneContactItem.getPhones())
                                    .setPhoto(phoneContactItem.getPhoto())
                            );
                        }
                    }
                }
            }
            event.setResponse(list);
        } catch (Exception e) {
            Log.e(NAME, e.getMessage());
            event.setErrorText(e.getMessage());
        } finally {
            if (cursor != null) {
                CloseUtils.close(cursor);
            }
        }
        return event;
    }

}
