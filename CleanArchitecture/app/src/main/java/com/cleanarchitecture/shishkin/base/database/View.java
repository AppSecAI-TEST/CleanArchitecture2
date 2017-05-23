package com.cleanarchitecture.shishkin.base.database;

import android.support.annotation.NonNull;

import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.criteria.Criteria;
import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.from.From;
import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.projection.Projection;

/**
 * A helper class to manage the database views.
 */
public final class View {

    public static View.RawBuilder forName(@NonNull final String name, @NonNull final String select_sql) {
        return new View.RawBuilder(name, select_sql);
    }

    public static final class RawBuilder {

        private final String mSelect;
        private final String mName;

        RawBuilder(@NonNull final String name, @NonNull final String select_sql) {
            mName = name;
            mSelect = select_sql;
        }

        @NonNull
        String toSql() {
            final StringBuilder sb = new StringBuilder();
            sb.append("CREATE VIEW " + mName);
            sb.append(" AS " + mSelect+";");
            return sb.toString();
        }

        public void create(@NonNull final IDatabase db) {
            final String sql = toSql();
            db.execSQL(sql);
        }

    }

    public static View.Builder forName(@NonNull final String name, @NonNull final QueryBuilder queryBuilder) {
        return new View.Builder(name, queryBuilder);
    }

    public static final class Builder {

        private final String mName;
        private final QueryBuilder mQueryBuilder;

        Builder(@NonNull final String name, @NonNull final QueryBuilder queryBuilder) {
            mName = name;
            mQueryBuilder = queryBuilder;
        }


        @NonNull
        public View.Builder distinct() {
            mQueryBuilder.distinct();
            return this;
        }

        @NonNull
        public View.Builder from(@NonNull String table) {
            mQueryBuilder.from(table);
            return this;
        }

        @NonNull
        public View.Builder from(@NonNull From from) {
            mQueryBuilder.from(from);
            return this;
        }

        @NonNull
        public View.Builder from(@NonNull QueryBuilder subQuery) {
            mQueryBuilder.from(subQuery);
            return this;
        }

        @NonNull
        public View.Builder groupBy(@NonNull String... columns) {
            mQueryBuilder.groupBy(columns);
            return this;
        }

        @NonNull
        public View.Builder groupBy(@NonNull Projection... projections){
            mQueryBuilder.groupBy(projections);
            return this;
        }

        @NonNull
        public View.Builder notDistinct() {
            mQueryBuilder.notDistinct();
            return this;
        }

        @NonNull
        public View.Builder orderByAscending(@NonNull String... columns){
            mQueryBuilder.orderByAscending(columns);
            return this;
        }

        @NonNull
        public View.Builder orderByAscendingIgnoreCase(@NonNull String... columns) {
            mQueryBuilder.orderByAscendingIgnoreCase(columns);
            return this;
        }

        @NonNull
        public View.Builder orderByDescending(@NonNull String... columns) {
            mQueryBuilder.orderByDescending(columns);
            return this;
        }

        @NonNull
        public View.Builder orderByDescendingIgnoreCase(@NonNull String... columns) {
            mQueryBuilder.orderByDescendingIgnoreCase(columns);
            return this;
        }

        @NonNull
        public View.Builder union(@NonNull QueryBuilder query){
            mQueryBuilder.union(query);
            return this;
        }

        @NonNull
        public View.Builder unionAll(@NonNull QueryBuilder query){
            mQueryBuilder.unionAll(query);
            return this;
        }

        @NonNull
        public View.Builder select(@NonNull String... columns) {
            mQueryBuilder.select(columns);
            return this;
        }

        @NonNull
        public View.Builder select(@NonNull Projection... projections){
            mQueryBuilder.select(projections);
            return this;
        }

        @NonNull
        public View.Builder whereAnd(@NonNull Criteria criteria){
            mQueryBuilder.whereAnd(criteria);
            return this;
        }

        @NonNull
        public View.Builder whereOr(@NonNull Criteria criteria) {
            mQueryBuilder.whereOr(criteria);
            return this;
        }

        @NonNull
        public View.Builder withDateFormat(@NonNull String format) {
            mQueryBuilder.withDateFormat(format);
            return this;
        }

        @NonNull
        public View.Builder withDateTimeFormat(@NonNull String format) {
            mQueryBuilder.withDateTimeFormat(format);
            return this;
        }

        @NonNull
        String toSql() {
            final String select = mQueryBuilder.build();
            final StringBuilder sb = new StringBuilder();
            sb.append("CREATE VIEW " + mName);
            sb.append(" AS " + select+";");
            return sb.toString();
        }

        public void create(@NonNull final IDatabase db) {
            final String sql = toSql();
            db.execSQL(sql);
        }
    }

    /**
     * Drops the existing view.
     *
     * @param db    the database.
     * @param view the name of view to be dropped.
     */
    public static void drop(@NonNull final IDatabase db, @NonNull final String view) {
        db.execSQL("DROP VIEW " + view + ";");
    }

    private View() {
    }

}
