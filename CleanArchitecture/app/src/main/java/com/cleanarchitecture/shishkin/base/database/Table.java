package com.cleanarchitecture.shishkin.base.database;

import android.support.annotation.NonNull;

import com.cleanarchitecture.shishkin.base.utils.TextUtilsExt;

import java.util.ArrayList;
import java.util.List;

/**
 * A helper class to manage the database tables.
 */
public final class Table {

    private static final String INDEX_SFX = "_idx";

    public static Builder forName(@NonNull final String name) {
        return new Builder(name);
    }

    public static final class Builder {

        private final String mName;
        private final List<Column> mColumns;
        private final List<Constraint> mConstraints;

        /* package */ Builder(@NonNull final String name) {
            mName = name;
            mColumns = new ArrayList<>();
            mConstraints = new ArrayList<>();
        }

        @NonNull
        public Builder addColumn(@NonNull final Column column) {
            mColumns.add(column);
            return this;
        }

        @NonNull
        public Builder addConstraint(@NonNull final Constraint constraint) {
            mConstraints.add(constraint);
            return this;
        }

        @NonNull
        /* package */ String toSql() {
            if (mColumns.isEmpty()) {
                throw new IllegalStateException("At least one column should be declared. " +
                        "Please see #addColumn for more details.");
            }

            final List<String> definitions = new ArrayList<>();
            for (final Column column : mColumns) {
                definitions.add(column.toSql());
            }
            for (final Constraint constraint : mConstraints) {
                definitions.add(constraint.toSql());
            }

            return "CREATE TABLE " + mName + "(" + TextUtilsExt.join(", ", definitions) + ");";
        }

        public void create(@NonNull final IDatabase db) {
            final String sql = toSql();
            db.execSQL(sql);
        }

    }

    public static final class Alter {

        private final String mName;
        private final List<Column> mAddedColumns = new ArrayList<>();

        public Alter(@NonNull final String name) {
            mName = name;
        }

        @NonNull
        public Alter addColumn(@NonNull final Column column) {
            mAddedColumns.add(column);
            return this;
        }

        public void execute(@NonNull final IDatabase db) {
            db.beginTransaction();
            try {
                for (final Column column : mAddedColumns) {
                    db.execSQL("ALTER TABLE " + mName + " ADD COLUMN " + column.toSql() + ";");
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

    }

    /**
     * Creates index in the database.
     *
     * @param db        The database.
     * @param table     The name of a table.
     * @param name      The name of index (suffix _idx will be appended to name automatically)
     * @param onColumns The array of column names in index.
     */
    public static void createIndex(@NonNull final IDatabase db, @NonNull final String table,
                                   @NonNull final String name, @NonNull final String[] onColumns) {
        final String sql = "CREATE INDEX " + name + INDEX_SFX + " ON " + table +
                "(" + TextUtilsExt.join(", ", onColumns) + ");";
        db.execSQL(sql);
    }

    /**
     * Drops the existing table.
     *
     * @param db    the database.
     * @param table the name of table to be dropped.
     */
    public static void drop(@NonNull final IDatabase db, @NonNull final String table) {
        db.execSQL("DROP TABLE " + table + ";");
    }

    private Table() {
    }

}
