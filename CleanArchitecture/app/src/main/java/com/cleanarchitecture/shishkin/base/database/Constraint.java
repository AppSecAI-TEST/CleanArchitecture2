package com.cleanarchitecture.shishkin.base.database;

import android.support.annotation.NonNull;

import com.cleanarchitecture.shishkin.base.utils.TextUtilsExt;

public abstract class Constraint {

    @NonNull
    public static UniqueConstraint unique(@NonNull final String name) {
        return new UniqueConstraint(name);
    }

    @NonNull
    public static PrimaryKeyConstraint primaryKey(@NonNull final String name) {
        return new PrimaryKeyConstraint(name);
    }

    @NonNull
    private final String mName;

    private Constraint(@NonNull final String name) {
        mName = name;
    }

    @NonNull
    /* package */ String toSql() {
        return "CONSTRAINT " + mName;
    }

    public static class UniqueConstraint extends IndexedColumnsConstraint {

        public UniqueConstraint(@NonNull final String name) {
            super(name, "UNIQUE");
        }

    }

    public static class PrimaryKeyConstraint extends IndexedColumnsConstraint {

        public PrimaryKeyConstraint(@NonNull final String name) {
            super(name, "PRIMARY KEY");
        }

    }

    public static class IndexedColumnsConstraint extends Constraint {

        @NonNull
        private final String mConstraint;

        private String[] mColumns = null;
        private ConflictResolution mConflictResolution = ConflictResolution.ABORT;

        /* package */ IndexedColumnsConstraint(@NonNull final String name, @NonNull final String constraint) {
            super(name);
            mConstraint = constraint;
        }

        @NonNull
        public IndexedColumnsConstraint withColumns(@NonNull final String... columns) {
            mColumns = columns;
            return this;
        }

        @NonNull
        public IndexedColumnsConstraint onConflict(@NonNull final ConflictResolution conflictResolution) {
            mConflictResolution = conflictResolution;
            return this;
        }

        @NonNull
        @Override
        /* package */ final String toSql() {
            if (mColumns == null) {
                throw new IllegalStateException("Constraint should contain at least one column");
            }
            return super.toSql() + " " + mConstraint + " "
                    + "(" + TextUtilsExt.join(", ", mColumns) + ") "
                    + mConflictResolution.toSql();
        }
    }

}
