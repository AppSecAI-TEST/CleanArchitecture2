package com.cleanarchitecture.shishkin.base.database.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.cleanarchitecture.shishkin.base.database.DatabaseCreator;
import com.cleanarchitecture.shishkin.base.database.IDataController;
import com.cleanarchitecture.shishkin.base.database.IDatabase;
import com.cleanarchitecture.shishkin.base.database.IDatabaseOpenHelper;
import com.cleanarchitecture.shishkin.base.utils.FileUtils;
import com.cleanarchitecture.shishkin.base.utils.StringUtils;
import com.github.snowdream.android.util.Log;
import com.google.common.io.Files;

import java.io.File;

/**
 * A helper class to manage database creation and version management.
 * Implementation refers to {@link SQLiteOpenHelper}.
 */
public class SQLiteDatabaseOpenHelper implements IDatabaseOpenHelper, IDataController {

    private static final String LOG_TAG = "SQLiteIDatabaseOpenHelper:";
    private final SQLiteAndroidOpenHelper mSQLiteOpenHelper;

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly. The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context         to use to open or create the database
     * @param name            of the database file, or null for an in-memory database
     * @param version         number of the database (starting at 1); if the database is older,
     *                        {@link DatabaseCreator#onUpgrade} will be used to upgrade the database;
     * @param databaseCreator a database lifecycle manager.
     */
    public SQLiteDatabaseOpenHelper(final Context context, final String name, final int version,
                                    final DatabaseCreator databaseCreator) {
        mSQLiteOpenHelper = new SQLiteAndroidOpenHelper(context, name, version, databaseCreator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized IDatabase getReadableDatabase() {
        return new SQLiteDatabaseWrapper(mSQLiteOpenHelper.getReadableDatabase());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized IDatabase getWritableDatabase() {
        return new SQLiteDatabaseWrapper(mSQLiteOpenHelper.getWritableDatabase());
    }

    @Override
    public void create(Context context) {
    }

    @Override
    public boolean backup(String dirBackup) {
        if (mSQLiteOpenHelper == null) {
            return false;
        }
        final String pathDb = mSQLiteOpenHelper.getReadableDatabase().getPath();
        if (StringUtils.isNullOrEmpty(pathDb)) {
            return false;
        }

        final File fileDb = new File(pathDb);
        final String nameDb = fileDb.getName();
        final String pathBackup = dirBackup + File.separator + nameDb;
        try {
            final File fileBackup = new File(pathBackup);
            final File fileBackupOld = new File(pathBackup + "1");
            if (fileDb.exists()) {
                if (fileBackup.exists()) {
                    final File dir = new File(dirBackup);
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
                                    } else {
                                        Files.copy(fileBackupOld, fileBackup);
                                    }
                                    Log.i(LOG_TAG, "Replace DB copy");
                                    return true;
                                }
                            }
                        }
                    }
                } else {
                    final File dir = new File(dirBackup);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    if (dir.exists()) {
                        Files.copy(fileDb, fileBackup);
                        if (fileBackup.exists()) {
                            Log.i(LOG_TAG, "Backup DB");
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG + ":backup", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean restore(String dirBackup) {
        if (mSQLiteOpenHelper == null) {
            return false;
        }
        final String pathDb = mSQLiteOpenHelper.getReadableDatabase().getPath();
        if (StringUtils.isNullOrEmpty(pathDb)) {
            return false;
        }

        final File fileDb = new File(pathDb);
        final String nameDb = fileDb.getName();
        final String pathBackup = dirBackup + File.separator + nameDb;
        final File fileBackup = new File(pathBackup);
        if (fileBackup.exists()) {
            try {
                if (fileDb.exists()) {
                    fileDb.delete();
                }
                if (!fileDb.exists()) {
                    Files.createParentDirs(fileDb);
                    final File dir = new File(fileDb.getParent());
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    if (dir.exists()) {
                        Files.copy(fileBackup, fileDb);
                        Log.i(LOG_TAG, "Restore DB from backup");
                        return true;
                    }
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
        return false;
    }

    @Override
    public int getVersion() {
        final SQLiteDatabase db = mSQLiteOpenHelper.getWritableDatabase();
        return db.getVersion();
    }

    @Override
    public boolean exists(final Context context, final String name) {
        try {
            final String pathDb = context.getDatabasePath(name).getAbsolutePath();
            if (StringUtils.isNullOrEmpty(pathDb)) {
                return false;
            }
            if (!FileUtils.isFileExists(pathDb)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void upgrade(Context context, int version) {
    }

    private static class SQLiteAndroidOpenHelper extends SQLiteOpenHelper {

        private DatabaseCreator mDatabaseCreator;
        private SQLiteDatabase mSQLiteDatabase;
        private boolean isCreating = false;

        public SQLiteAndroidOpenHelper(final Context context, final String name, final int version,
                                       @NonNull final DatabaseCreator databaseCreator) {
            super(context, name, null, version);
            mDatabaseCreator = databaseCreator;

        }

        @Override
        public void onCreate(final SQLiteDatabase db) {
            isCreating = true;
            mSQLiteDatabase = db;
            mDatabaseCreator.onCreate(new SQLiteDatabaseWrapper(db));
            mSQLiteDatabase = null;
            isCreating = false;
        }

        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            mDatabaseCreator.onUpgrade(new SQLiteDatabaseWrapper(db), oldVersion, newVersion);
        }

        @Override
        public synchronized SQLiteDatabase getWritableDatabase() {
            if (isCreating && mSQLiteDatabase != null) {
                return mSQLiteDatabase;
            }
            return super.getWritableDatabase();
        }

        @Override
        public synchronized SQLiteDatabase getReadableDatabase() {
            if (isCreating && mSQLiteDatabase != null) {
                return mSQLiteDatabase;
            }
            return super.getReadableDatabase();
        }

    }

}
