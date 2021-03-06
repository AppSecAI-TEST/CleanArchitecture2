package com.cleanarchitecture.shishkin.application.data.dao;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.api.data.ExtError;
import com.cleanarchitecture.shishkin.api.data.Result;
import com.cleanarchitecture.shishkin.application.data.cursor.PhoneContactCursor;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.common.content.dao.AbstractReadOnlyDAO;
import com.cleanarchitecture.shishkin.common.utils.CloseUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import java.util.LinkedList;
import java.util.List;

public class PhoneContactDAO extends AbstractReadOnlyDAO<PhoneContactItem> {

    private static final String LOG_TAG = "PhoneContactDAO:";

    public static final Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
    private ContentResolver mContentResolver;

    public interface Columns {
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    }

    public static final String[] PROJECTION = {
            Columns._ID,
            Columns.DISPLAY_NAME,
            Columns.HAS_PHONE_NUMBER
    };

    public PhoneContactDAO(final Context context) {
        super(context);

        mContentResolver = context.getContentResolver();
    }

    @NonNull
    @Override
    protected Uri getTableUri() {
        return CONTENT_URI;
    }

    @Nullable
    @Override
    protected String[] getProjection() {
        return PROJECTION;
    }

    @NonNull
    @Override
    public PhoneContactItem getItemFromCursor(final Cursor cursor) {
        final PhoneContactItem contactItem = new PhoneContactItem();
        contactItem.setId(getString(cursor, Columns._ID));
        contactItem.setName(getString(cursor, Columns.DISPLAY_NAME));
        if (getInteger(cursor, Columns.HAS_PHONE_NUMBER) > 0) {
            final Cursor phoneCursor = mContentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[]{contactItem.getId()}, null);
            if (AbstractReadOnlyDAO.isCursorValid(phoneCursor)) {
                final StringBuilder sb = new StringBuilder();
                while (phoneCursor.moveToNext()) {
                    String phone = phoneCursor.getString(phoneCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if (!StringUtils.isNullOrEmpty(phone)) {
                        sb.append(";" + phone + ";");
                    }
                }
                phoneCursor.close();
                contactItem.setPhones(sb.toString());
            }

            final Uri contactUri = ContentUris.withAppendedId(CONTENT_URI, StringUtils.toLong(contactItem.getId()));
            final Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
            final Cursor photoCursor = mContentResolver.query(photoUri,
                    new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
            if (AbstractReadOnlyDAO.isCursorValid(photoCursor)) {
                if (photoCursor.moveToFirst()) {
                    contactItem.setPhoto(photoUri.toString());
                }
                photoCursor.close();
            }
        }
        return contactItem;
    }

    public Result<List<PhoneContactItem>> getItems(final Context context, final Cursor cursor, final int rows) {
        final Result<List<PhoneContactItem>> result = new Result<>();
        final LinkedList<PhoneContactItem> list = new LinkedList<>();
        try {
            int i = 0;
            while (cursor.moveToNext() && i < rows) {
                list.add(getItemFromCursor(cursor));
                i++;
            }
            result.setResult(list);
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
            result.setError(new ExtError().addError(LOG_TAG, context.getString(R.string.error_read_phone_contacts)));
        }
        return result;
    }

    public Result<List<PhoneContactItem>> getItems(final Context context) {
        final Result<List<PhoneContactItem>> result = new Result<>();
        final LinkedList<PhoneContactItem> list = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = PhoneContactCursor.getCursor(context);
            if (AbstractReadOnlyDAO.isCursorValid(cursor)) {
                while (cursor.moveToNext()) {
                    list.add(getItemFromCursor(cursor));
                }
            }
            result.setResult(list);
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
            result.setError(new ExtError().addError(LOG_TAG, context.getString(R.string.error_read_phone_contacts)));
        } finally {
            CloseUtils.close(cursor);
        }
        return result;
    }

}
