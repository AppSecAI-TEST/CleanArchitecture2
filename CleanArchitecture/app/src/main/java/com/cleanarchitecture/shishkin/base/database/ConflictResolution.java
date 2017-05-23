package com.cleanarchitecture.shishkin.base.database;

import android.support.annotation.NonNull;

/**
 * SQLite defines five constraint conflict resolution algorithms as follows:
 */
public enum ConflictResolution {

    /**
     * When a constraint violation occurs, an immediate ROLLBACK occurs,
     * thus ending the current transaction, and the command aborts with
     * a return code of SQLITE_CONSTRAINT. If no transaction is active
     * (other than the implied transaction that is created on every command)
     * then this algorithm works the same as ABORT.
     */
    ROLLBACK("ON CONFLICT ROLLBACK"),

    /**
     * When a constraint violation occurs, the command backs out any prior
     * changes it might have made and aborts with a return code of SQLITE_CONSTRAINT.
     * But no ROLLBACK is executed so changes from prior commands within
     * the same transaction are preserved. This is the default behavior for SQLite.
     */
    ABORT("ON CONFLICT ABORT"),

    /**
     * When a constraint violation occurs, the command aborts with
     * a return code SQLITE_CONSTRAINT. But any changes to the database that
     * the command made prior to encountering the constraint violation are preserved
     * and are not backed out. For example, if an UPDATE statement encountered
     * a constraint violation on the 100th row that it attempts to update,
     * then the first 99 row changes are preserved but change to rows 100 and beyond never occur.
     */
    FAIL("ON CONFLICT FAIL"),

    /**
     * When a constraint violation occurs, the one row that contains the constraint violation
     * is not inserted or changed. But the command continues executing normally.
     * Other rows before and after the row that contained the constraint violation
     * continue to be inserted or updated normally. No error is returned.
     */
    IGNORE("ON CONFLICT IGNORE"),

    /**
     * When a UNIQUE constraint violation occurs, the pre-existing row that caused
     * the constraint violation is removed prior to inserting or updating the current row.
     * Thus the insert or update always occurs. The command continues executing normally.
     * No error is returned.
     */
    REPLACE("ON CONFLICT REPLACE");

    @NonNull
    private final String mSql;

    ConflictResolution(@NonNull final String sql) {
        mSql = sql;
    }

    @NonNull
    /* package */ String toSql() {
        return mSql;
    }

}