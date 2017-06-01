package com.cleanarchitecture.shishkin.base.repository;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.application.data.cursor.ContactCursor;
import com.cleanarchitecture.shishkin.application.data.dao.PhoneContactDAO;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.database.CleanArchitectureDb;
import com.cleanarchitecture.shishkin.application.database.item.Contact;
import com.cleanarchitecture.shishkin.base.controller.Controllers;
import com.cleanarchitecture.shishkin.base.controller.EventBusController;
import com.cleanarchitecture.shishkin.base.database.dao.AbstractReadOnlyDAO;
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

    public synchronized List<PhoneContactItem> getContacts() {
        if (!ApplicationUtils.checkPermission(Manifest.permission.READ_CONTACTS)) {
            EventBusController.getInstance().post(new UseCaseRequestPermissionEvent(Manifest.permission.READ_CONTACTS));
            return null;
        }

        final Context context = ApplicationController.getInstance();
        if (context == null) {
            return null;
        }

        final CleanArchitectureDb db = (CleanArchitectureDb)Controllers.getInstance().getRepository().getDbProvider().getDb();
        if (db == null) {
            return null;
        }

        Cursor cursor = null;
        try {
            final LinkedList<PhoneContactItem> list = new LinkedList<>();
            final PhoneContactDAO phoneContactDAO = new PhoneContactDAO(context);
            cursor = ContactCursor.getCursor(context);
            if (AbstractReadOnlyDAO.isCursorValid(cursor)) {
                while (cursor.moveToNext()) {
                    final PhoneContactItem phoneContactItem = phoneContactDAO.getItemFromCursor(cursor);
                    if (phoneContactItem != null) {
                        list.add(phoneContactItem);
                        if (db.contactDao().countContact(phoneContactItem.getId()) == 0) {
                            db.contactDao().insert(new Contact()
                                    .setRowId(phoneContactItem.getId())
                                    .setName(phoneContactItem.getName())
                                    .setPhones(phoneContactItem.getPhones())
                                    .setPhoto(phoneContactItem.getPhoto())
                            );
                        }
                    }
                }
                return list;
            }
            return list;
        } catch (Exception e) {
            Log.e(NAME, e.getMessage());
        } finally {
            if (cursor != null) {
                CloseUtils.close(cursor);
            }
        }
        return null;
    }

}
