package com.cleanarchitecture.shishkin.base.database;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.content.ContentProviderUtils;

/**
 * A helper class to manage database lifecycle events like creation and upgrade.
 */
public abstract class DatabaseCreator {
    private static final String[] COUNT_1_PROJECTION = new String[]{"count(1) as " + IBaseColumns._COUNT};

    public DatabaseCreator() {
    }

    /**
     * Called when the database is created for the first time.
     * Please call that method in your own {@link IDatabaseOpenHelper}.
     */
    public void onCreate(final IDatabase db) {
        onCreate(ApplicationController.getInstance(), db);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param context The global environment info.
     * @param db      The database.
     */
    public abstract void onCreate(final Context context, final IDatabase db);

    /**
     * Called when the database needs to be upgraded.
     * Please call that method in your own {@link IDatabaseOpenHelper}.
     */
    public boolean onUpgrade(final IDatabase db, final int oldVersion, final int newVersion) {
        int toVersion = oldVersion + 1;
        while (toVersion <= newVersion) {
            if (onUpgrade(ApplicationController.getInstance(), db, toVersion)) {
                toVersion++;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * Notice that method may be called many times and make sure you've correctly
     * handle incremental updates to a given version.
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction. If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param context   The global environment info.
     * @param db        The database.
     * @param toVersion The incremental database version to update to.
     */
    public abstract boolean onUpgrade(final Context context, final IDatabase db, final int toVersion);

    /**
     * controls the existence of the table in contentprovider
     *
     * @return true if the table is exists.
     */
    public static boolean isTableExists(final Context context, final Uri tableUri) {
        try {
            final Cursor cursor = context.getContentResolver().query(tableUri, COUNT_1_PROJECTION,
                    null, null, null);
            if (cursor != null) {
                cursor.close();
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean isViewExists(final Context context, final Uri viewUri) {
        try {
            final Cursor cursor = context.getContentResolver().query(viewUri, COUNT_1_PROJECTION,
                    null, null, null);
            if (cursor != null) {
                cursor.close();
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static Uri getTableUri(final String authority, final String table) {
        return ContentProviderUtils.createContentUri(authority, table);
    }


}
