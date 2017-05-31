package com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite;

import com.cleanarchitecture.shishkin.base.database.QueryBuilder;
import com.cleanarchitecture.shishkin.base.database.querybuilder.QueryBuilderUtils;
import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.criteria.Criteria;
import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.from.From;
import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.order.Order;
import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.projection.AliasedProjection;
import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.projection.Projection;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class SqliteQueryBuilder implements QueryBuilder {
    private List<Projection> projections;
    private From from;
    private Criteria criteria;
    private List<Projection> groupBy;
    private List<Order> orderBy;
    private int skip;
    private int take;
    private boolean distinct;

    private List<SqliteQueryBuilder> unionQueries;
    private boolean unionAll;

    private DateTimeFormatter dateFormat;
    private DateTimeFormatter dateTimeFormat;

    public SqliteQueryBuilder() {
        this(QueryBuilderUtils.DATE_FORMATTER, QueryBuilderUtils.DATE_TIME_FORMATTER);
    }

    public SqliteQueryBuilder(String dateFormat, String dateTimeFormat) {
        this(DateTimeFormat.forPattern(dateFormat), DateTimeFormat.forPattern(dateTimeFormat));
    }

    public SqliteQueryBuilder(DateTimeFormatter dateFormat, DateTimeFormatter dateTimeFormat) {
        projections = new ArrayList<Projection>();
        from = null;
        criteria = null;
        groupBy = new ArrayList<Projection>();
        orderBy = new ArrayList<Order>();
        skip = -1;
        take = -1;
        distinct = false;

        unionQueries = new ArrayList<SqliteQueryBuilder>();
        unionAll = false;

        this.dateFormat = dateFormat;
        this.dateTimeFormat = dateTimeFormat;
    }

    public SqliteQueryBuilder withDateFormat(String format) {
        return withDateFormat(DateTimeFormat.forPattern(format));
    }

    public SqliteQueryBuilder withDateFormat(DateTimeFormatter format) {
        this.dateFormat = format;
        return this;
    }

    public SqliteQueryBuilder withDateTimeFormat(String format) {
        return withDateTimeFormat(DateTimeFormat.forPattern(format));
    }

    public SqliteQueryBuilder withDateTimeFormat(DateTimeFormatter format) {
        this.dateTimeFormat = format;
        return this;
    }

    @Override
    public SqliteQueryBuilder select(String... columns) {
        if (columns == null)
            return this;

        return select(QueryBuilderUtils.buildColumnProjections(columns));
    }

    @Override
    public SqliteQueryBuilder select(Projection... projections) {
        if (projections == null)
            return this;

        for (int i = 0; i < projections.length; i++) {
            this.projections.add(projections[i]);
        }

        return this;
    }

    @Override
    public SqliteQueryBuilder from(String table) {
        return from(From.table(table));
    }

    @Override
    public SqliteQueryBuilder from(QueryBuilder subQuery) {
        return from(From.subQuery((SqliteQueryBuilder) subQuery));
    }

    @Override
    public SqliteQueryBuilder from(From from) {
        if (from != null)
            this.from = from;

        return this;
    }

    @Override
    public SqliteQueryBuilder whereAnd(Criteria criteria) {
        if (criteria != null) {
            if (this.criteria == null)
                this.criteria = criteria;
            else
                this.criteria = this.criteria.and(criteria);
        }

        return this;
    }

    @Override
    public SqliteQueryBuilder whereOr(Criteria criteria) {
        if (criteria != null) {
            if (this.criteria == null)
                this.criteria = criteria;
            else
                this.criteria = this.criteria.or(criteria);
        }

        return this;
    }

    @Override
    public SqliteQueryBuilder groupBy(String... columns) {
        if (columns == null)
            return this;

        return groupBy(QueryBuilderUtils.buildColumnProjections(columns));
    }

    @Override
    public SqliteQueryBuilder groupBy(Projection... projections) {
        if (projections == null)
            return this;

        for (int i = 0; i < projections.length; i++) {
            this.groupBy.add(projections[i]);
        }

        return this;
    }

    public SqliteQueryBuilder clearGroupBy() {
        this.groupBy.clear();
        return this;
    }

    @Override
    public SqliteQueryBuilder orderByAscending(String... columns) {
        if (columns == null)
            return this;

        return orderByAscending(QueryBuilderUtils.buildColumnProjections(columns));
    }

    @Override
    public SqliteQueryBuilder orderByAscending(Projection... projections) {
        if (projections == null)
            return this;

        for (int i = 0; i < projections.length; i++) {
            this.orderBy.add(Order.orderByAscending(projections[i]));
        }

        return this;
    }

    @Override
    public SqliteQueryBuilder orderByDescending(String... columns) {
        if (columns == null)
            return this;

        return orderByDescending(QueryBuilderUtils.buildColumnProjections(columns));
    }

    @Override
    public SqliteQueryBuilder orderByDescending(Projection... projections) {
        if (projections == null)
            return this;

        for (int i = 0; i < projections.length; i++) {
            this.orderBy.add(Order.orderByDescending(projections[i]));
        }

        return this;
    }

    @Override
    public SqliteQueryBuilder orderByAscendingIgnoreCase(String... columns) {
        if (columns == null)
            return this;

        return orderByAscendingIgnoreCase(QueryBuilderUtils.buildColumnProjections(columns));
    }

    @Override
    public SqliteQueryBuilder orderByAscendingIgnoreCase(Projection... projections) {
        if (projections == null)
            return this;

        for (int i = 0; i < projections.length; i++) {
            this.orderBy.add(Order.orderByAscendingIgnoreCase(projections[i]));
        }

        return this;
    }

    @Override
    public SqliteQueryBuilder orderByDescendingIgnoreCase(String... columns) {
        if (columns == null)
            return this;

        return orderByDescendingIgnoreCase(QueryBuilderUtils.buildColumnProjections(columns));
    }

    @Override
    public SqliteQueryBuilder orderByDescendingIgnoreCase(Projection... projections) {
        if (projections == null)
            return this;

        for (int i = 0; i < projections.length; i++) {
            this.orderBy.add(Order.orderByDescendingIgnoreCase(projections[i]));
        }

        return this;
    }

    public SqliteQueryBuilder clearOrderBy() {
        this.orderBy.clear();
        return this;
    }

    @Override
    public SqliteQueryBuilder offset(int skip) {
        this.skip = skip;
        return this;
    }

    @Override
    public SqliteQueryBuilder offsetNone() {
        this.skip = -1;
        return this;
    }

    @Override
    public SqliteQueryBuilder limit(int take) {
        this.take = take;
        return this;
    }

    @Override
    public SqliteQueryBuilder limitAll() {
        this.take = -1;
        return this;
    }

    @Override
    public SqliteQueryBuilder distinct() {
        this.distinct = true;
        return this;
    }

    @Override
    public SqliteQueryBuilder notDistinct() {
        this.distinct = false;
        return this;
    }

    @Override
    public SqliteQueryBuilder union(QueryBuilder query) {
        ((SqliteQueryBuilder) query).unionAll = false;
        unionQueries.add((SqliteQueryBuilder) query);

        return this;
    }

    @Override
    public SqliteQueryBuilder unionAll(QueryBuilder query) {
        ((SqliteQueryBuilder) query).unionAll = true;
        unionQueries.add((SqliteQueryBuilder) query);

        return this;
    }

    @Override
    public String build() {
        StringBuilder sb = new StringBuilder();

        buildSelectClause(sb);

        buildFromClause(sb);

        buildWhereClause(sb);

        buildGroupByClause(sb);

        buildUnionClause(sb);

        buildOrderByClause(sb);

        buildTakeClause(sb);

        buildSkipClause(sb);

        return sb.toString();
    }

    private void buildSkipClause(StringBuilder sb) {
        if (skip > 0) {
            sb.append(" OFFSET ");
            sb.append(skip);
        }
    }

    private void buildTakeClause(StringBuilder sb) {
        if (take > 0) {
            sb.append(" LIMIT ");
            sb.append(take);
        }
    }

    private void buildOrderByClause(StringBuilder sb) {
        if (orderBy.size() > 0) {
            sb.append(" ORDER BY ");

            for (Order o : orderBy) {
                sb.append(o.build());
                sb.append(", ");
            }

            sb.setLength(sb.length() - 2); // removes the ", " from the last entry
        }
    }

    private void buildUnionClause(StringBuilder sb) {
        List<Order> oldOrderBy;
        int oldSkip;
        int oldTake;

        for (SqliteQueryBuilder union : unionQueries) {
            sb.append(union.unionAll ? " UNION ALL " : " UNION ");

            oldOrderBy = union.orderBy;
            oldSkip = union.skip;
            oldTake = union.take;

            union.orderBy = new ArrayList<Order>();
            union.skip = -1;
            union.take = -1;

            sb.append(union.build());

            union.orderBy = oldOrderBy;
            union.skip = oldSkip;
            union.take = oldTake;
        }
    }

    private void buildGroupByClause(StringBuilder sb) {
        if (groupBy.size() > 0) {
            sb.append(" GROUP BY ");

            for (Projection p : groupBy) {
                if (p instanceof AliasedProjection)
                    p = ((AliasedProjection) p).removeAlias();

                sb.append(p.build());
                sb.append(", ");
            }

            sb.setLength(sb.length() - 2); // removes the ", " from the last entry
        }
    }

    private void buildWhereClause(StringBuilder sb) {
        if (criteria != null) {
            sb.append("WHERE ");
            sb.append(criteria.build());
        }
    }

    private void buildFromClause(StringBuilder sb) {
        if (from != null) {
            sb.append("FROM ");
            sb.append(from.build());
            sb.append(" ");
        }
    }

    private void buildSelectClause(StringBuilder sb) {
        sb.append("SELECT ");

        if (distinct)
            sb.append("DISTINCT ");

        if (projections.size() <= 0) {
            sb.append("*");
        } else {
            for (Projection p : projections) {
                sb.append(p.build());
                sb.append(", ");
            }

            sb.setLength(sb.length() - 2); // removes the ", " from the last entry
        }

        sb.append(" ");
    }

    public List<Object> buildParameters() {
        List<Object> ret = new ArrayList<Object>();
        List<Order> oldOrderBy;
        int oldSkip;
        int oldTake;

        buildSelectClauseParameters(ret);

        if (from != null)
            ret.addAll(from.buildParameters());

        if (criteria != null)
            ret.addAll(criteria.buildParameters());

        for (Projection p : groupBy) {
            ret.addAll(p.buildParameters());
        }

        for (SqliteQueryBuilder union : unionQueries) {
            oldOrderBy = union.orderBy;
            oldSkip = union.skip;
            oldTake = union.take;

            union.orderBy = new ArrayList<Order>();
            union.skip = -1;
            union.take = -1;

            ret.addAll(union.buildParameters());

            union.orderBy = oldOrderBy;
            union.skip = oldSkip;
            union.take = oldTake;
        }

        for (Order o : orderBy) {
            ret.addAll(o.buildParameters());
        }

        preProcessDateValues(ret);
        return ret;
    }

    private void buildSelectClauseParameters(List<Object> ret) {
        for (Projection p : projections) {
            ret.addAll(p.buildParameters());
        }
    }

    public String toDebugSqlString() {
        List<Object> parameters = buildParameters();
        String saida = build();

        if (parameters != null) {
            for (Object p : parameters) {
                if (p == null)
                    saida = saida.replaceFirst("\\?", "NULL");
                else
                    saida = saida.replaceFirst("\\?", escapeSQLString(QueryBuilderUtils.toString(p)));
            }
        }

        return saida;
    }

    private void preProcessDateValues(List<Object> values) {
        Object value;
        int index = 0;

        while (index < values.size()) {
            value = values.get(index);

            if (value instanceof LocalDateTime) {
                values.remove(index);
                values.add(index, QueryBuilderUtils.dateToString(((LocalDateTime) value), dateTimeFormat));
            } else if (value instanceof LocalDate) {
                values.remove(index);
                values.add(index, QueryBuilderUtils.dateToString(((LocalDate) value), dateFormat));
            }

            index++;
        }
    }

    private String escapeSQLString(String sqlString) {
        // Copied from Android source: DatabaseUtils.appendEscapedSQLString
        StringBuilder sb = new StringBuilder();
        sb.append('\'');

        if (sqlString.indexOf('\'') != -1) {
            int length = sqlString.length();
            for (int i = 0; i < length; i++) {
                char c = sqlString.charAt(i);
                if (c == '\'') {
                    sb.append('\'');
                }
                sb.append(c);
            }
        } else
            sb.append(sqlString);

        sb.append('\'');
        return sb.toString();
    }
}
