package com.cleanarchitecture.shishkin.application.database.dao;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cleanarchitecture.shishkin.application.database.content.CleanArchitectureContentProvider;
import com.cleanarchitecture.shishkin.application.database.item.SqliteMasterItem;
import com.cleanarchitecture.shishkin.base.content.ContentProviderUtils;
import com.cleanarchitecture.shishkin.base.database.dao.AbstractReadOnlyDAO;

public class SqliteMasterDAO extends AbstractReadOnlyDAO<SqliteMasterItem> {

    public static final String TABLE = "sqlite_master";

    public interface Columns {
        String type = "type";
        String name = "name";
        String tbl_name = "tbl_name";
    }

    public static final Uri CONTENT_URI = ContentProviderUtils
            .createContentUri(CleanArchitectureContentProvider.AUTHORITY, TABLE);

    public static final String[] PROJECTION = {
            Columns.type,
            Columns.name,
            Columns.tbl_name
    };

    public SqliteMasterDAO(final Context context) {
        super(context);
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
    protected SqliteMasterItem getItemFromCursor(final Cursor cursor) {
        return new SqliteMasterItem()
                .setType(getString(cursor, Columns.type))
                .setName(getString(cursor, Columns.name))
                .setTblName(getString(cursor, Columns.tbl_name));
    }

}
