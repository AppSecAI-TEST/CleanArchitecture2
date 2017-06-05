package com.cleanarchitecture.shishkin.application.data.dao;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.base.content.dao.AbstractReadOnlyDAO;
import com.cleanarchitecture.shishkin.base.utils.StringUtils;

public class PhoneContactDAO extends AbstractReadOnlyDAO<PhoneContactItem> {

    public static final Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
    public static final Uri CONTENT_DATA_URI = ContactsContract.Data.CONTENT_URI;

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
}
