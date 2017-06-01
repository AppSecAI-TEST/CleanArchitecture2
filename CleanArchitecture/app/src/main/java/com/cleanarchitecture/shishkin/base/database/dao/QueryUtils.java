package com.cleanarchitecture.shishkin.base.database.dao;

import android.support.annotation.NonNull;

public class QueryUtils {

    /**
     * Prepare SQL ? placeholders for IN selection.
     *
     * @param column     The name of column.
     * @param argsNumber The number of ? placeholders.
     * @return The formatted SQL selection for IN queries.
     */
    @NonNull
    public static String in(@NonNull final String column, final int argsNumber) {
        return in(column, argsNumber, true);
    }

    /**
     * Prepare SQL ? placeholders for NOT IN selection.
     *
     * @param column     The name of column.
     * @param argsNumber The number of ? placeholders.
     * @return The formatted SQL selection for NOT IN queries.
     */
    @NonNull
    public static String notIn(@NonNull final String column, final int argsNumber) {
        return in(column, argsNumber, false);
    }

    @NonNull
    /* package */ public static String in(@NonNull final String column, final int argsNumber, final boolean in) {
        final StringBuilder inClause = new StringBuilder();

        if (argsNumber > 0) {
            inClause.append(column).append(in ? " " : " not ").append("in (");
            for (int i = 0; i < argsNumber; i++) {
                if (i > 0) {
                    inClause.append(", ");
                }
                inClause.append("?");
            }
            inClause.append(")");
        }

        return inClause.toString();
    }

    private QueryUtils() {
    }

}
