package com.cleanarchitecture.shishkin.common.content.dao;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cleanarchitecture.shishkin.common.content.IBaseColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * Class provides read-only access to any data in content provider.
 * Implementation of object-relation mapping should be provided by child.
 *
 * @param <E> The class that used as data entity for this data access object.
 */
public abstract class AbstractReadOnlyDAO<E> {
    private static final String[] COUNT_1_PROJECTION = new String[]{"count(1) as " + IBaseColumns._COUNT};

    /**
     * Checks if the cursor valid
     */
    public static boolean isCursorValid(@Nullable final Cursor cursor) {
        return (cursor != null);
    }

    /**
     * Prepares string array to use it as selection arguments in the database queries.
     *
     * @param args The selection arguments, in the order that they
     *             appear in the selection. The values will be bound as Strings.
     * @return The string array with provided arguments.
     */
    @NonNull
    public static String[] prepareArguments(@Nullable final Object... args) {
        String[] params = {};
        if (args != null) {
            params = new String[args.length];
            for (int i = 0; i < args.length; i++) {
                params[i] = String.valueOf(args[i]);
            }
        }
        return params;
    }

    /**
     * Returns the non-null value of the requested column as a String.
     * Notice that cursor should be moved to an absolute position.
     *
     * @param c      cursor.
     * @param column the name of the target column.
     * @return the non-null value of that column as a String.
     * @throws IllegalStateException when value is null.
     */
    @NonNull
    public static String getString(final Cursor c, final String column) {
        final String nullableString = getNullableString(c, column);
        if (nullableString == null) {
            throw new IllegalStateException("Column " + column + " should not be null");
        }
        return nullableString;
    }

    /**
     * Returns the nullable value of the requested column as a String.
     * Notice that cursor should be moved to an absolute position.
     *
     * @param c      cursor.
     * @param column the name of the target column.
     * @return the nullable value of that column as a String.
     */
    @Nullable
    public static String getNullableString(final Cursor c, final String column) {
        final int columnIndex = c.getColumnIndex(column);
        return c.getString(columnIndex);
    }

    /**
     * Returns the value of the requested column as an int.
     * Notice that cursor should be moved to an absolute position.
     *
     * @param c      cursor.
     * @param column the name of the target column.
     * @return the value of that column as an int.
     */
    public static int getInteger(final Cursor c, final String column) {
        final int columnIndex = c.getColumnIndex(column);
        return c.getInt(columnIndex);
    }

    /**
     * Returns the nullable value of the requested column as an Integer.
     * Notice that cursor should be moved to an absolute position.
     *
     * @param c      cursor.
     * @param column the name of the target column.
     * @return the nullable value of that column as an Integer.
     */
    @Nullable
    public static Integer getNullableInteger(final Cursor c, final String column) {
        final int columnIndex = c.getColumnIndex(column);
        Integer integer = null;
        if (!c.isNull(columnIndex)) {
            integer = c.getInt(columnIndex);
        }
        return integer;
    }

    /**
     * Returns the value of the requested column as a long.
     * Notice that cursor should be moved to an absolute position.
     *
     * @param c      cursor.
     * @param column the name of the target column.
     * @return the value of that column as a long.
     */
    public static long getLong(final Cursor c, final String column) {
        final int columnIndex = c.getColumnIndex(column);
        return c.getLong(columnIndex);
    }

    /**
     * Returns the nullable value of the requested column as a Long.
     * Notice that cursor should be moved to an absolute position.
     *
     * @param c      cursor.
     * @param column the name of the target column.
     * @return the nullable value of that column as a Long.
     */
    @Nullable
    public static Long getNullableLong(final Cursor c, final String column) {
        final int columnIndex = c.getColumnIndex(column);
        Long l = null;
        if (!c.isNull(columnIndex)) {
            l = c.getLong(columnIndex);
        }
        return l;
    }

    /**
     * Returns the value of the requested column as a double.
     * Notice that cursor should be moved to an absolute position.
     *
     * @param c      cursor.
     * @param column the name of the target column.
     * @return the value of that column as a double.
     */
    public static double getDouble(final Cursor c, final String column) {
        final int columnIndex = c.getColumnIndex(column);
        return c.getDouble(columnIndex);
    }

    /**
     * Returns the nullable value of the requested column as a Double.
     * Notice that cursor should be moved to an absolute position.
     *
     * @param c      cursor.
     * @param column the name of the target column.
     * @return the nullable value of that column as a Double.
     */
    @Nullable
    public static Double getNullableDouble(final Cursor c, final String column) {
        final int columnIndex = c.getColumnIndex(column);
        Double d = null;
        if (!c.isNull(columnIndex)) {
            d = c.getDouble(columnIndex);
        }
        return d;
    }

    private final ContentResolver mContentResolver;

    /**
     * Creates instance of data access class.
     *
     * @param context The {@link Context} that will be used to obtain {@link ContentResolver}.
     */
    protected AbstractReadOnlyDAO(@NonNull final Context context) {
        mContentResolver = context.getContentResolver();
    }

    /**
     * Returns a {@link ContentResolver} instance for your application's package.
     */
    public ContentResolver getContentResolver() {
        return mContentResolver;
    }

    /**
     * Query the given content uri, returning a non-null {@link List} of entities.
     *
     * @return a non-null {@link List} of data entities.
     * @see #getTableUri()
     */
    @NonNull
    public List<E> getAll() {
        return get(null, null, null);
    }

    /**
     * Query the given content uri, returning a non-null {@link List} of entities.
     *
     * @param selection     A filter declaring which rows to return, formatted as an
     *                      SQL WHERE clause (excluding the WHERE itself). Passing null
     *                      will return all rows for the given table.
     * @param selectionArgs You may include ?s in selection, which will be
     *                      replaced by the values from selectionArgs, in order that they
     *                      appear in the selection. The values will be bound as Strings.
     * @param sortOrder     How to order the rows, formatted as an SQL ORDER BY clause
     *                      (excluding the ORDER BY itself). Passing null will use the
     *                      default sort order, which may be unordered.
     * @return a non-null {@link List} of data entities.
     */
    @NonNull
    protected List<E> get(@Nullable final String selection, @Nullable final String[] selectionArgs,
                          @Nullable final String sortOrder) {
        return get(getTableUri(), getProjection(), selection, selectionArgs, sortOrder);
    }

    @NonNull
    private List<E> get(@NonNull final Uri uri, @Nullable final String[] projection,
                        @Nullable final String selection, @Nullable final String[] selectionArgs,
                        @Nullable final String sortOrder) {
        final Cursor cursor = mContentResolver.query(uri, projection,
                selection, selectionArgs, sortOrder);
        return getItemsFromCursor(cursor);
    }

    /**
     * Implement this method to provide content uri.
     *
     * @return a non-null content uri
     */
    @NonNull
    protected abstract Uri getTableUri();

    /**
     * Implement this method to provide a list of which columns to return during query.
     *
     * @return a nullable array of column names.
     */
    @Nullable
    protected abstract String[] getProjection();

    @NonNull
    private List<E> getItemsFromCursor(@Nullable final Cursor cursor) {
        final List<E> items = new ArrayList<E>();
        if (isCursorValid(cursor)) {
            if (cursor.moveToFirst()) {
                do {
                    items.add(getItemFromCursor(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return items;
    }

    /**
     * <p>
     * Implement this method to provide cursor-to-entity mapping.
     * </p>
     * <p>Here is the standard idiom for entity mapping:
     * <p/>
     * <pre>
     *   public class MyDataDAO extends AbstractReadOnlyDAO&lt;MyData&gt; {
     *
     *     ...
     *
     *     protected MyData getItemFromCursor(final Cursor cursor) {
     *         MyData myData = new MyData();
     *         myData.setInteger(getInteger(cursor, Columns.INTEGER_COLUMN));
     *         myData.setString(getString(cursor, Columns.STRING_COLUMN));
     *         return myData;
     *     }
     *
     *   }
     * </pre>
     *
     * @param cursor cursor
     * @return non-null data entity.
     */
    @NonNull
    protected abstract E getItemFromCursor(final Cursor cursor);

    /**
     * controls the existence of the table column in contentprovider
     *
     * @return true if the table column is exists.
     */
    public boolean isColumnExists(String column) {
        String[] columns = {column};
        try {
            final Cursor cursor = getContentResolver().query(getTableUri(), columns,
                    null, null, column + " ASC LIMIT 1");
            if (isCursorValid(cursor)) {
                cursor.close();
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }


}
