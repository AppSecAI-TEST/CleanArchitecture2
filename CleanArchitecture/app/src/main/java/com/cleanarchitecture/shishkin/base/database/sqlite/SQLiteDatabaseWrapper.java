package com.cleanarchitecture.shishkin.base.database.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.cleanarchitecture.shishkin.base.database.IDatabase;
import com.cleanarchitecture.shishkin.base.utils.StringUtils;
import com.github.snowdream.android.util.Log;
import com.google.common.io.Files;

import java.io.File;


/**
 * A wrapper over Android default SQLite database.
 * Exposes methods to manage a SQLite database.
 */
public class SQLiteDatabaseWrapper implements IDatabase {
    private static final String LOG_TAG = "SQLiteDatabaseWrapper:";

    private final SQLiteDatabase mDatabase;

    /**
     * Creates SQLite database wrapper.
     *
     * @param database a SQLiteDatabase instance.
     */
    public SQLiteDatabaseWrapper(@NonNull final SQLiteDatabase database) {
        mDatabase = database;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cursor query(final String table, final String[] columns, final String selection,
                        final String[] selectionArgs, final String groupBy, final String having,
                        final String orderBy) {
        return mDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long insert(final String table, final String nullColumnHack, final ContentValues values) {
        return mDatabase.insert(table, nullColumnHack, values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int update(final String table, final ContentValues values,
                      final String whereClause, final String[] whereArgs) {
        return mDatabase.update(table, values, whereClause, whereArgs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int delete(final String table, final String whereClause, final String[] whereArgs) {
        return mDatabase.delete(table, whereClause, whereArgs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execSQL(final String sql) {
        mDatabase.execSQL(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execSQL(final String sql, final Object[] bindArgs) {
        mDatabase.execSQL(sql, bindArgs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beginTransaction() {
        mDatabase.beginTransaction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTransactionSuccessful() {
        mDatabase.setTransactionSuccessful();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endTransaction() {
        mDatabase.endTransaction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return new File(mDatabase.getPath()).getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void backup(String path) {
        if (StringUtils.isNullOrEmpty(path)) {
            return;
        }

        final String pathBackup = path + File.separator + getName();
        try {
            final File fileDb = new File(mDatabase.getPath());
            final File fileBackup = new File(pathBackup);
            final File fileBackupOld = new File(pathBackup + "1");
            if (fileDb.exists()) {
                if (fileBackup.exists()) {
                    final File dir = new File(path);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    if (dir.exists()) {
                        if (fileBackupOld.exists()) {
                            fileBackupOld.delete();
                        }
                        if (!fileBackupOld.exists()) {
                            Files.copy(fileBackup, fileBackupOld);
                            if (fileBackupOld.exists()) {
                                fileBackup.delete();
                                if (!fileBackup.exists()) {
                                    Files.copy(fileDb, fileBackup);
                                    if (fileBackup.exists()) {
                                        fileBackupOld.delete();
                                        return;
                                    } else {
                                        Files.copy(fileBackupOld, fileBackup);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    final File dir = new File(path);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    if (dir.exists()) {
                        Files.copy(fileDb, fileBackup);
                        if (fileBackup.exists()) {
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void restore(String path) {
        final String pathBackup = path + File.separator + getName();
        final File fileBackup = new File(pathBackup);
        final File fileDb = new File(mDatabase.getPath());
        try {
            if (fileBackup.exists()) {
                Files.createParentDirs(fileDb);
                final File dir = new File(fileDb.getParent());
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                if (dir.exists()) {
                    Files.copy(fileBackup, fileDb);
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }


}