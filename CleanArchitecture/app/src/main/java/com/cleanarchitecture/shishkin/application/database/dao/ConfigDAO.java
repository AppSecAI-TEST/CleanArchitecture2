package com.cleanarchitecture.shishkin.application.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cleanarchitecture.shishkin.application.database.content.CleanArchitectureContentProvider;
import com.cleanarchitecture.shishkin.application.database.item.ConfigItem;
import com.cleanarchitecture.shishkin.base.content.ContentProviderUtils;
import com.cleanarchitecture.shishkin.base.database.IBaseColumns;
import com.cleanarchitecture.shishkin.base.database.dao.AbstractIdentifyDAO;

public class ConfigDAO extends AbstractIdentifyDAO<String, ConfigItem> {

    public static final String TABLE = "Config";

    public interface Columns extends IBaseColumns {
        String RowId = "RowId";
        String Version = "Version";
    }

    public static final Uri CONTENT_URI = ContentProviderUtils
            .createContentUri(CleanArchitectureContentProvider.AUTHORITY, TABLE);

    public static final String[] PROJECTION = {
            Columns.RowId,
            Columns.Version
    };

    public ConfigDAO(final Context context) {
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
    protected ConfigItem getItemFromCursor(final Cursor cursor) {
        return new ConfigItem()
                .setId(getString(cursor, Columns.RowId))
                .setVersion(getInteger(cursor, Columns.Version));
    }

    @NonNull
    @Override
    public ContentValues toContentValues(@NonNull final ConfigItem entity) {
        final ContentValues values = new ContentValues();
        values.put(Columns.RowId, entity.getId());
        values.put(Columns.Version, entity.getVersion());
        return values;
    }

    @Override
    protected String parseKey(@NonNull final String key) {
        return key;
    }

    @NonNull
    @Override
    protected String[] newArray(final int size) {
        return new String[size];
    }

}
