package com.cleanarchitecture.shishkin.base.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

/**
 * A helper class to declare columns in the database.
 *
 * @see Table
 */

public final class Column {

    private static final String COLUMN_TYPE_REAL = "REAL";
    private static final String COLUMN_TYPE_INTEGER = "INTEGER";
    private static final String COLUMN_TYPE_BLOB = "BLOB";
    private static final String COLUMN_TYPE_TEXT = "TEXT";

    @StringDef({ COLUMN_TYPE_REAL, COLUMN_TYPE_INTEGER, COLUMN_TYPE_BLOB, COLUMN_TYPE_TEXT })
    private @interface ColumnType {
    }

    /**
     * Creates a TEXT column with a given name.
     */
    @NonNull
    public static Column text(@NonNull final String name) {
        return new Column(name, COLUMN_TYPE_TEXT);
    }

    /**
     * Creates an INTEGER column with a given name.
     */
    @NonNull
    public static Column integer(@NonNull final String name) {
        return new Column(name, COLUMN_TYPE_INTEGER);
    }

    /**
     * Creates a REAL column with a given name.
     */
    @NonNull
    public static Column real(@NonNull final String name) {
        return new Column(name, COLUMN_TYPE_REAL);
    }

    /**
     * Creates a BLOB column with a given name.
     */
    @NonNull
    public static Column blob(@NonNull final String name) {
        return new Column(name, COLUMN_TYPE_BLOB);
    }

    private String mName;
    private String mType;
    private boolean mIsPrimary = false;
    private boolean mNotNull = false;
    private boolean mAutoIncrement = false;

    @Nullable
    private Default mDefault = null;

    private boolean mUnique = false;
    private ConflictResolution mUniqueConflictResolution = ConflictResolution.ABORT;

    private Column(@NonNull final String name, @ColumnType @NonNull final String type) {
        mName = name;
        mType = type;
    }

    @NonNull
    public Column primaryKey() {
        mIsPrimary = true;
        mNotNull = false;
        return this;
    }

    @NonNull
    public Column autoIncrement() {
        mIsPrimary = true;
        mAutoIncrement = true;
        mNotNull = false;
        return this;
    }

    @NonNull
    public Column notNull() {
        if (!mIsPrimary) {
            mNotNull = true;
        }
        return this;
    }

    @NonNull
    public Column defaultValue(@Nullable final Default def) {
        mDefault = def;
        return this;
    }

    @NonNull
    public Column unique(@NonNull final ConflictResolution conflictResolution) {
        mUnique = true;
        mUniqueConflictResolution = conflictResolution;
        return this;
    }

    public String getName() {
        return mName;
    }

    @NonNull
    /* package */ String toSql() {
        final StringBuilder sb = new StringBuilder();
        sb.append(mName).append(" ");
        sb.append(mType);
        if (mIsPrimary) {
            sb.append(" PRIMARY KEY");
        }
        if (mAutoIncrement) {
            if (COLUMN_TYPE_INTEGER.equals(mType)) {
                sb.append(" AUTOINCREMENT");
            } else {
                throw new IllegalArgumentException("Only integer fields support " +
                        "autoincrement values");
            }
        }
        if (mNotNull) {
            sb.append(" NOT NULL");
        }
        if (mUnique) {
            sb.append(" UNIQUE ").append(mUniqueConflictResolution.toSql());
        }
        if (mDefault != null) {
            sb.append(" ").append(mDefault.toSql());
        }
        return sb.toString();
    }
}
