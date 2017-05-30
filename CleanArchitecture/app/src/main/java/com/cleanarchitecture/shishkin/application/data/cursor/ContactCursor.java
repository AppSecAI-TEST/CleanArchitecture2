package com.cleanarchitecture.shishkin.application.data.cursor;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.cleanarchitecture.shishkin.application.data.dao.PhoneContactDAO;
import com.cleanarchitecture.shishkin.base.utils.StringUtils;
import com.github.snowdream.android.util.Log;

public class ContactCursor {
    private final static String LOG_TAG = "ContactCursor:";

    private ContactCursor() {
    }

    public static Cursor getCursor(@NonNull final Context context) {
        final String selection = PhoneContactDAO.Columns.HAS_PHONE_NUMBER + " > 0";
        Cursor cur = null;
        try {
            final ContentResolver cr = context.getContentResolver();
            if (cr != null) {
                cur = cr.query(PhoneContactDAO.CONTENT_URI, PhoneContactDAO.PROJECTION, selection, null, "upper(" + PhoneContactDAO.Columns.DISPLAY_NAME + ") asc");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return cur;
    }

    public static Cursor getCursor(@NonNull final Context context, final String search) {
        Cursor cur = null;
        try {
            final ContentResolver cr = context.getContentResolver();
            if (cr != null) {
                String selection = PhoneContactDAO.Columns.HAS_PHONE_NUMBER + " > 0";
                if (!StringUtils.isNullOrEmpty(search)) {
                    selection += " and " + PhoneContactDAO.Columns.DISPLAY_NAME + " like '%"+search+"%'";
                }
                cur = cr.query(PhoneContactDAO.CONTENT_URI, PhoneContactDAO.PROJECTION, selection, null, "upper(" + PhoneContactDAO.Columns.DISPLAY_NAME + ") asc");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return cur;
    }

}
