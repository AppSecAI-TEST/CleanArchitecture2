package com.cleanarchitecture.shishkin.application.database;

import android.content.Context;
import android.database.Cursor;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.application.database.dao.ConfigDAO;
import com.cleanarchitecture.shishkin.application.database.dao.SqliteMasterDAO;
import com.cleanarchitecture.shishkin.application.database.item.ConfigItem;
import com.cleanarchitecture.shishkin.base.database.IDatabase;
import com.cleanarchitecture.shishkin.base.database.sqlite.SQLiteDatabaseOpenHelper;
import com.github.snowdream.android.util.Log;

public class CleanArchitectureDatabaseHelper extends SQLiteDatabaseOpenHelper {

    private static final String LOG_TAG = "CleanArchitectureDatabaseHelper:";

    public static final String DATABASE_NAME = "clean_architecture.db";
    public static final int DATABASE_VERSION = 1;

    public CleanArchitectureDatabaseHelper(final Context context) {
        super(context, DATABASE_NAME, DATABASE_VERSION, new CleanArchitectureDatabaseCreator());
    }

    @Override
    public void create(Context context) {
        try {
            final Cursor cursor = context.getContentResolver().query(SqliteMasterDAO.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    @Override
    public int getVersion() {
        int version = 0;
        final ConfigDAO configDAO = new ConfigDAO(ApplicationController.getInstance().getApplicationContext());
        ConfigItem item = null;
        try {
            item = configDAO.get("1");
        } catch (Exception e) {
        }
        if (item == null) {
            configDAO.insert(new ConfigItem("1", DATABASE_VERSION));
            try {
                item = configDAO.get("1");
            } catch (Exception e) {
            }
            if (item == null) {
                version = item.getVersion();
            }
        } else {
            version = item.getVersion();
        }
        return version;
    }

    @Override
    public void upgrade(Context context, int newVersion) {
        create(context);

        IDatabase db = null;
        try {
            db = getWritableDatabase();
        } catch (Exception e) {
        }

        if (db != null) {
            final CleanArchitectureDatabaseCreator creator = new CleanArchitectureDatabaseCreator();
            creator.onUpgrade(db, getVersion(), newVersion);
        }
    }

}
