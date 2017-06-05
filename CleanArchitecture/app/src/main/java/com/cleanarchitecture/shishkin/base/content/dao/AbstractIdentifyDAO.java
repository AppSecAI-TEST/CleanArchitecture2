package com.cleanarchitecture.shishkin.base.content.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.cleanarchitecture.shishkin.base.content.IBaseColumns;
import com.cleanarchitecture.shishkin.base.utils.TextUtilsExt;

import java.util.Collections;
import java.util.List;

/**
 * Class provides read-write access and object-relation mapping to any data in content provider.
 *
 * @param <K> The class of data entity's key.
 * @param <E> The class that used as data entity for this data access object.
 */
public abstract class AbstractIdentifyDAO<K, E extends IIdentify<K>> extends AbstractReadOnlyDAO<E> {

    private static final String[] COUNT_1_PROJECTION = new String[]{"count(1) as " + IBaseColumns._COUNT};
    private static final String WHERE_BASE_COLUMNS_ID = IBaseColumns._ID + "=?";

    /**
     * Returns normalized id that can be used in content uris.
     *
     * @param id entity id as is.
     * @return normalized id.
     */
    protected static String normalizeId(@NonNull final String id) {
        return id.replaceAll("/", "");
    }

    /**
     * Creates instance of data access class
     *
     * @param context The {@link Context} that will be used to obtain {@link ContentResolver}.
     */
    public AbstractIdentifyDAO(@NonNull final Context context) {
        super(context);
    }

    /**
     * Inserts data to content provider.
     *
     * @param entity entity to be inserted in database.
     * @return inserted content uri.
     */
    @Nullable
    public Uri insert(@NonNull final E entity) {
        return insert(getTableUri(), toContentValues(entity));
    }

    /**
     * Inserts a given {@link ContentValues} to content provider.
     *
     * @param values columns-values map.
     * @return inserted content uri.
     */
    @Nullable
    public Uri insert(@NonNull final ContentValues values) {
        return insert(getTableUri(), values);
    }

    @Nullable
    private Uri insert(@NonNull final Uri uri, @NonNull final ContentValues values) {
        return getContentResolver().insert(uri, values);
    }

    /**
     * Inserts a {@link List} of entities in one transaction.
     *
     * @param entities a non-null {@link List} of entities.
     * @return the number of values that were inserted.
     */
    public int bulkInsert(@NonNull final List<E> entities) {
        return bulkInsert(getTableUri(), entities);
    }

    private int bulkInsert(@NonNull final Uri uri, @NonNull final List<E> entities) {
        final ContentValues[] contentValues = toContentValues(entities);
        return getContentResolver().bulkInsert(uri, contentValues);
    }

    /**
     * Updates a given entity record in the database.
     * Notice that entity should have non-null id to be updated.
     *
     * @param entity a data entity.
     * @return the number of rows affected.
     */
    public int update(@NonNull final E entity) {
        return update(entity.getId(), toContentValues(entity));
    }

    /**
     * Updates a given {@link ContentValues} columns in the database.
     *
     * @param id     an id of entity.
     * @param values columns-values map.
     * @return the number of rows affected.
     */
    public int update(@NonNull final K id, @NonNull final ContentValues values) {
        final String normalizedId = normalizeId(String.valueOf(id));
        return update(getTableUri(), values, WHERE_BASE_COLUMNS_ID, new String[]{normalizedId});
    }

    private int update(@NonNull final Uri uri, @NonNull ContentValues values,
                       @Nullable final String where,
                       @Nullable final String[] selectionArgs) {
        return getContentResolver().update(uri, values, where, selectionArgs);
    }

    /**
     * Updates a given data entity if entity exists in the database, inserts new record otherwise.
     *
     * @param entity a data entity.
     * @return {@code true} if entity was updated or inserted, {@code false} otherwise.
     */
    public boolean insertOrUpdate(@NonNull final E entity) {
        return (update(entity) > 0 || insert(entity) != null);
    }

    /**
     * Updates a given column/value pair if entity with given id exists in the database,
     * inserts new record otherwise.
     *
     * @param id     an entity id.
     * @param values an column/value pair.
     * @return {@code true} if entity was updated or inserted, {@code false} otherwise.
     */
    public boolean insertOrUpdate(@NonNull final K id, @NonNull final ContentValues values) {
        return (update(id, values) > 0 || insert(values) != null);
    }

    /**
     * <p>
     * Implement that method to map your data entity to {@link ContentValues}.
     * </p>
     * <p>Here is the standard idiom for entity mapping:
     * <p/>
     * <pre>
     *   public class MyDataDAO extends AbstractIdentifyDAO<String, MyData> {
     *
     *     ...
     *
     *     protected ContentValues toContentValues(final MyData entity) {
     *         ContentValues values = new ContentValues();
     *         values.put(Columns._ID, entity.getId());
     *         values.put(Columns.INTEGER_COLUMN, entity.getInteger());
     *         values.put(Columns.STRING_COLUMN, entity.getString());
     *         return values;
     *     }
     *
     *   }
     * </pre>
     *
     * @param entity a data entity.
     * @return an column/value pair.
     */
    @NonNull
    public abstract ContentValues toContentValues(@NonNull final E entity);

    @NonNull
    private ContentValues[] toContentValues(@NonNull final List<E> entities) {
        final int size = entities.size();

        final ContentValues[] contentValues = new ContentValues[size];
        for (int i = 0; i < size; i++) {
            contentValues[i] = toContentValues(entities.get(i));
        }
        return contentValues;
    }

    /**
     * Deletes rows from the database.
     *
     * @param uri           a table content uri.
     * @param selection     an optional filter to match rows to update
     * @param selectionArgs an optional binding arguments.
     * @return the number of rows affected.
     */
    protected int delete(@NonNull final Uri uri,
                         @Nullable final String selection,
                         @Nullable final String[] selectionArgs) {
        return getContentResolver().delete(uri, selection, selectionArgs);
    }

    /**
     * Deletes entity from the database.
     *
     * @param entity a data entity.
     * @return the number of rows affected.
     */
    public int delete(@NonNull final E entity) {
        return delete(entity.getId());
    }

    /**
     * Deletes entity from the database by a given id.
     *
     * @param id an entity id.
     * @return the number of rows affected.
     */
    public int delete(final K id) {
        int deletedRecords = 0;
        final String normalizedId = normalizeId(String.valueOf(id));
        if (!TextUtils.isEmpty(normalizedId)) {
            deletedRecords = delete(getTableUri(), WHERE_BASE_COLUMNS_ID,
                    prepareArguments(normalizedId));
        }
        return deletedRecords;
    }

    /**
     * Deletes entities with a given ids from the database.
     *
     * @param ids an array of entity id.
     * @return the number of rows affected.
     */
    public int deleteAll(@NonNull final K[] ids) {
        return deleteAll(ids, true);
    }

    /**
     * Deletes entities which <b>are not</b> in a given ids from the database.
     *
     * @param ids an array of entity id that will not deleted.
     * @return the number of rows affected.
     */
    public int deleteNotIn(@NonNull final K[] ids) {
        return deleteAll(ids, false);
    }

    private int deleteAll(@NonNull final K[] ids, final boolean in) {
        int deletedRecords = 0;

        final int length = ids.length;
        if (length > 0) {
            final String[] normalizedIds = new String[length];
            for (int i = 0; i < length; i++) {
                normalizedIds[i] = normalizeId(String.valueOf(ids[i]));
            }

            final String where = QueryUtils.in(IBaseColumns._ID, length, in);
            deletedRecords = delete(getTableUri(), where, normalizedIds);
        }
        return deletedRecords;
    }

    /**
     * Deletes entities from the database.
     *
     * @param entities The entities to be deleted.
     * @return the number of rows affected.
     */
    public int deleteAll(@NonNull final List<E> entities) {
        return deleteAll(entities, true);
    }

    /**
     * Deletes all entities from the database except a given one.
     *
     * @param entities The entities that will not be deleted.
     * @return the number of rows affected.
     */
    public int deleteNotIn(@NonNull final List<E> entities) {
        return deleteAll(entities, false);
    }

    private int deleteAll(@NonNull final List<E> entities, final boolean in) {
        final int size = entities.size();
        int deletedRecords = 0;
        if (size > 0) {
            final K[] ids = newArray(size);
            for (int i = 0; i < size; i++) {
                ids[i] = entities.get(i).getId();
            }
            deletedRecords = deleteAll(ids, in);
        }
        return deletedRecords;
    }

    /**
     * Return an data entity for a given entity id.
     *
     * @param id an entity id.
     * @return an data entity or {@code null}.
     */
    @Nullable
    public E get(final K id) {
        final String normalizedId = normalizeId(String.valueOf(id));
        final List<E> candidates = get(WHERE_BASE_COLUMNS_ID, prepareArguments(normalizedId), null);
        return (candidates.isEmpty() ? null : candidates.get(0));
    }

    /**
     * Return an data entities for a given ids.
     *
     * @param ids an entity ids.
     * @return an entities.
     */
    @NonNull
    public List<E> get(final List<K> ids) {
        return get(ids, null);
    }

    /**
     * Return sorted data entities for a given ids.
     *
     * @param ids    an entity ids.
     * @param sortBy How to order the rows, formatted as an SQL ORDER BY clause
     *               (excluding the ORDER BY itself). Passing null will use the
     *               default sort order, which may be unordered.
     * @return an entities.
     */
    @NonNull
    public List<E> get(final List<K> ids, final String sortBy) {
        final int size = ids.size();
        final K[] idsArray = ids.toArray(newArray(size));
        return get(idsArray, sortBy);
    }

    @NonNull
    private List<E> get(final K[] ids, final String sortBy) {
        final int length = ids.length;
        if (length > 0) {
            final String[] normalizedIds = new String[length];
            for (int i = 0; i < length; i++) {
                normalizedIds[i] = normalizeId(String.valueOf(ids[i]));
            }

            final String where = QueryUtils.in(IBaseColumns._ID, length);
            return get(where, normalizedIds, sortBy);

        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Returns total number of rows in the table.
     *
     * @return number of rows in the table.
     */
    public int getCount() {
        return getCount(null);
    }

    /**
     * Returns number of rows with a given id.
     *
     * @param id an entity id.
     * @return number of rows.
     */
    public int getCount(@Nullable final K id) {
        return getCount(id, true);
    }

    /**
     * Return number of rows.
     *
     * @param id an entity id.
     * @param in a boolean flag to indicate which rows should be included to result.
     * @return a number of rows.
     */
    public int getCount(@Nullable final K id, final boolean in) {
        String selection = null;
        String[] selectionArgs = null;
        final String normalizedId = (id == null ? null : normalizeId(String.valueOf(id)));
        if (!TextUtilsExt.isEmpty(normalizedId)) {
            selection = QueryUtils.in(IBaseColumns._ID, 1, in);
            selectionArgs = prepareArguments(normalizedId);
        }

        return getCount(selection, selectionArgs);
    }

    /**
     * Return number of rows that match a given selection.
     *
     * @param selection     an optional filter to match rows.
     * @param selectionArgs an optional binding arguments.
     * @return number of rows.
     */
    protected int getCount(@Nullable final String selection, @Nullable final String[] selectionArgs) {
        final Cursor cursor = getContentResolver().query(getTableUri(), COUNT_1_PROJECTION,
                selection, selectionArgs, null);

        int count = 0;
        if (isCursorValid(cursor)) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(cursor.getColumnIndex(IBaseColumns._COUNT));
            }
            cursor.close();
        }

        return count;
    }

    /**
     * Checks if entity with a given id exists in the database.
     *
     * @param id an entity id.
     * @return {@code true} if entity exists, {@code false} otherwise.
     */
    public boolean exists(final K id) {
        return getCount(id) > 0;
    }

    /**
     * Implement this method to create an array of entity key class.
     *
     * @param size a number of keys.
     * @return an array of entity key class.
     */
    @NonNull
    protected abstract K[] newArray(final int size);

    /**
     * Returns an entity id from a given content uri.
     *
     * @param contentUri an inserted content uri.
     * @return a parsed entity id.
     */
    @Nullable
    public K parseId(@Nullable final Uri contentUri) {
        final String last = (contentUri != null ? contentUri.getLastPathSegment() : null);
        return last == null ? null : parseKey(last);
    }

    /**
     * Implement that method to parse entity key.
     *
     * @param key a String representation of an entity id.
     * @return a parsed entity id.
     */
    protected abstract K parseKey(@NonNull final String key);

}