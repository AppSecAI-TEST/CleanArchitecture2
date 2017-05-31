package com.cleanarchitecture.shishkin.base.database;

import android.content.Context;
import android.database.sqlite.SQLiteException;

/**
 * A helper class to manage creation and opening a database.
 */
public interface IDatabaseOpenHelper {

    /**
     * Create and/or open a database.  This will be the same object returned by
     * {@link #getWritableDatabase} unless some problem, such as a full disk,
     * requires the database to be opened read-only.  In that case, a read-only
     * database object will be returned.  If the problem is fixed, a future call
     * to {@link #getWritableDatabase} may succeed, in which case the read-only
     * database object will be closed and the read/write object will be returned
     * in the future.
     * <p>
     * <p class="caution">Like {@link #getWritableDatabase}, this method may
     * take a long time to return, so you should not call it from the
     * application main thread, including from
     * {@link android.content.ContentProvider#onCreate ContentProvider.onCreate()}.
     *
     * @return a database object.
     * @throws SQLiteException if the database cannot be opened
     */
    IDatabase getReadableDatabase();

    /**
     * Create and/or open a database that will be used for reading and writing.
     * <p>
     * <p>Once opened successfully, the database is cached, so you can
     * call this method every time you need to write to the database.
     * Errors such as bad permissions or a full disk may cause this method
     * to fail, but future attempts may succeed if the problem is fixed.</p>
     * <p>
     * <p class="caution">Database upgrade may take a long time, you
     * should not call this method from the application main thread, including
     * from {@link android.content.ContentProvider#onCreate ContentProvider.onCreate()}.
     *
     * @return a read/write database object
     * @throws SQLiteException if the database cannot be opened for writing
     */
    IDatabase getWritableDatabase();

    int getVersion();

    void upgrade(Context context, int version);

}
