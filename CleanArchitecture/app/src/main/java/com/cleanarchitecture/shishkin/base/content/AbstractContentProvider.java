package com.cleanarchitecture.shishkin.base.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.cleanarchitecture.shishkin.base.database.IBaseColumns;
import com.cleanarchitecture.shishkin.base.database.IDatabase;
import com.cleanarchitecture.shishkin.base.database.IDatabaseOpenHelper;
import com.cleanarchitecture.shishkin.base.utils.TextUtilsExt;
import com.github.snowdream.android.util.Log;

/**
 * Content providers are one of the primary building blocks of Android applications, providing
 * content to applications. They encapsulate data and provide it to applications through the single
 * {@link ContentResolver} interface.
 * <p/>
 * <p>When a request is made via a {@link ContentResolver} the system inspects the authority
 * of the given URI and passes the request to the content provider registered with the authority.
 * The content provider can interpret the rest of the URI however it wants.</p>
 * <p/>
 * <p class="caution">Data access methods (such as {@link #insert} and
 * {@link #update}) may be called from many threads at once, and must be thread-safe.
 * Other methods (such as {@link #onCreate}) are only called from the application
 * main thread, and must avoid performing lengthy operations. See the method
 * descriptions for their expected thread behavior.</p>
 * <p/>
 * <p>Requests to {@link ContentResolver} are automatically forwarded to the appropriate
 * ContentProvider instance, so subclasses don't have to worry about the details of
 * cross-process calls.</p>
 * <p/>
 * <div class="special reference">
 * <h3>Developer Guides</h3>
 * <p>For more information about using content providers, read the
 * <a href="{@docRoot}guide/topics/providers/content-providers.html">Content Providers</a>
 * developer guide.</p>
 */
public abstract class AbstractContentProvider extends ContentProvider {
    private static final String LOG_TAG = "AbstractContentProvider:";

    private static final int MATCH_DIR = 1;
    private static final int MATCH_ID = 2;

    private static final String WHERE_ID = IBaseColumns._ID + "=?";

    private UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private IDatabaseOpenHelper mDatabaseHelper;

    @Override
    public final void attachInfo(final Context context, final ProviderInfo info) {
        onAttachInfo(context, info);
        super.attachInfo(context, info);
    }

    /**
     * After being instantiated, this is called to tell the content provider
     * about itself.
     * Please register your tables by using {@link #addUri(String, String, boolean)} and
     * {@link ProviderInfo#authority}.
     *
     * @param context The context this provider is running in
     * @param info    Registered information about this content provider
     */
    public abstract void onAttachInfo(final Context context, final ProviderInfo info);


    /**
     * Register table in content provider.
     *
     * @param authority The name provider is published under content://
     * @param table     The table name
     * @param hasChild  The flag to determine if the rows have their own
     *                  content uris for a given table.
     */
    public void addUri(final String authority, final String table, final boolean hasChild) {
        mUriMatcher.addURI(authority, table, MATCH_DIR);
        if (hasChild) {
            mUriMatcher.addURI(authority, table + "/*", MATCH_ID);
        }
    }

    /**
     * {@hide}
     */
    @SuppressWarnings("all")
    @Override
    public boolean onCreate() {
        final Context context = getContext();
        mDatabaseHelper = onCreateDatabaseOpenHelper(context);
        return (mDatabaseHelper != null);
    }

    /**
     * Implement this to initialize your database helper on startup.
     * This method is called for all registered content providers on the
     * application main thread at application launch time.  It must not perform
     * lengthy operations, or application startup will be delayed.
     *
     * @param context The context this provider is running in.
     */
    @NonNull
    public abstract IDatabaseOpenHelper onCreateDatabaseOpenHelper(@NonNull final Context context);

    /**
     * {@hide}
     */
    @Override
    public String getType(@NonNull final Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case MATCH_DIR:
                return "vnd.android.cursor.dir/vnd." + uri.getAuthority() + "." + getTableFromUri(uri);

            case MATCH_ID:
                return "vnd.android.cursor.dir/item." + uri.getAuthority() + "." + getTableFromUri(uri);

            default:
                Log.e(LOG_TAG, "Unknown uri: " + uri);
                return null;
        }
    }

    private String getTableFromUri(final Uri uri) {
        return uri.getPathSegments().get(0);
    }

    /**
     * {@hide}
     */
    @Override
    public Cursor query(@NonNull final Uri uri, final String[] projection, final String where,
                        final String[] whereArgs, final String sort) {
        switch (mUriMatcher.match(uri)) {
            case MATCH_DIR:
                return withNotificationUri(uri, query(getTableFromUri(uri), projection, where, whereArgs, sort));

            case MATCH_ID:
                return withNotificationUri(uri, queryById(getTableFromUri(uri), projection, uri.getLastPathSegment()));

            default:
                Log.e(LOG_TAG, "Unknown uri: " + uri);
                return null;
        }
    }

    @NonNull
    private Cursor query(final String table, final String[] projection, final String where,
                         final String[] whereArgs, final String sort) {
        return mDatabaseHelper.getReadableDatabase()
                .query(table, projection, where, whereArgs, null, null, sort);
    }

    @NonNull
    private Cursor queryById(final String table, final String[] projection, final String id) {
        return mDatabaseHelper.getReadableDatabase()
                .query(table, projection, WHERE_ID, new String[] { id },
                        null, null, null);
    }

    /**
     * {@hide}
     */
    @Override
    public Uri insert(@NonNull final Uri uri, final ContentValues values) {
        switch (mUriMatcher.match(uri)) {
            case MATCH_DIR:
                return notifyInsert(uri, insert(getTableFromUri(uri), values));

            case MATCH_ID:
                notifyChange(uriWithoutId(uri), updateById(getTableFromUri(uri), uri.getLastPathSegment(), values));
                return uri;

            default:
                Log.e(LOG_TAG, "Unknown uri: " + uri);
                return null;
        }
    }

    private String insert(final String table, final ContentValues values) {
        final String uid = values.getAsString(IBaseColumns._ID);
        final long rowId = mDatabaseHelper.getWritableDatabase()
                .insert(table, null, values);
        if (rowId <= 0) {
            Log.e(LOG_TAG, "Failed to insert row into table '" + table + "'");
            return null;
        }
        return uid;
    }

    /**
     * {@hide}
     */
    @Override
    public int bulkInsert(@NonNull final Uri uri, @NonNull final ContentValues[] values) {
        switch (mUriMatcher.match(uri)) {
            case MATCH_DIR:
                return notifyChange(uri, bulkInsert(getTableFromUri(uri), values));

            case MATCH_ID:
            default:
                Log.e(LOG_TAG, "Unknown uri: " + uri);
                return -1;
        }
    }

    private int bulkInsert(final String table, final ContentValues[] bulkValues) {
        final IDatabase db = mDatabaseHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (final ContentValues values : bulkValues) {
                final String id = values.getAsString(IBaseColumns._ID);
                if (TextUtilsExt.isEmpty(id) || updateById(table, id, values) <= 0) {
                    insert(table, values);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return bulkValues.length;
    }

    /**
     * {@hide}
     */
    @Override
    public int update(@NonNull final Uri uri, final ContentValues values, final String where, final String[] whereArgs) {
        switch (mUriMatcher.match(uri)) {
            case MATCH_DIR:
                return notifyChange(uri, update(getTableFromUri(uri), values, where, whereArgs));

            case MATCH_ID:
                return notifyChange(uri, updateById(getTableFromUri(uri), uri.getLastPathSegment(), values));

            default:
                Log.e(LOG_TAG, "Unknown uri: " + uri);
                return -1;
        }
    }

    private int update(final String table, final ContentValues values,
                       final String where, final String[] whereArgs) {
        return mDatabaseHelper.getWritableDatabase()
                .update(table, values, where, whereArgs);
    }

    private int updateById(final String table, final String id, final ContentValues values) {
        final IDatabase db = mDatabaseHelper.getWritableDatabase();
        int affectedRows = db.update(table, values, WHERE_ID, new String[] { id });
        if (affectedRows < 1) {
            if (db.insert(table, null, values) > 0) {
                ++affectedRows;
            }
        }
        return affectedRows;
    }

    /**
     * {@hide}
     */
    @Override
    public int delete(@NonNull final Uri uri, final String where, final String[] whereArgs) {
        switch (mUriMatcher.match(uri)) {
            case MATCH_DIR:
                return notifyChange(uri, delete(getTableFromUri(uri), where, whereArgs));

            case MATCH_ID:
                return notifyChange(uri, deleteById(getTableFromUri(uri), uri.getLastPathSegment()));

            default:
                Log.e(LOG_TAG, "Unknown uri: " + uri);
                return -1;
        }
    }

    private int delete(final String table, final String where, final String[] whereArgs) {
        return mDatabaseHelper.getWritableDatabase().delete(table, where, whereArgs);
    }

    private int deleteById(final String table, final String id) {
        return mDatabaseHelper.getWritableDatabase()
                .delete(table, WHERE_ID, new String[] { id });
    }

    @SuppressWarnings("all")
    private Cursor withNotificationUri(@NonNull final Uri uri, final Cursor cursor) {
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @SuppressWarnings("all")
    private Uri notifyInsert(@NonNull final Uri uri, @NonNull final String uid) {
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.withAppendedPath(uri, uid);
    }

    @SuppressWarnings("all")
    private int notifyChange(@NonNull final Uri uri, final int affectedRows) {
        if (affectedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }

    @NonNull
    private Uri uriWithoutId(@NonNull final Uri uri) {
        return Uri.parse(uri.getScheme() + "://" + uri.getAuthority() + "/" + getTableFromUri(uri));
    }

}

