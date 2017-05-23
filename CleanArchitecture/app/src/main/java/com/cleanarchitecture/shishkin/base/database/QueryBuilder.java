package com.cleanarchitecture.shishkin.base.database;

import android.support.annotation.NonNull;

import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.criteria.Criteria;
import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.from.From;
import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.projection.Projection;

public interface QueryBuilder {

    String build();

    QueryBuilder distinct();

    QueryBuilder notDistinct();

    QueryBuilder from(@NonNull String table);

    QueryBuilder from(@NonNull From from);

    QueryBuilder from(@NonNull QueryBuilder subQuery);

    QueryBuilder groupBy(@NonNull String... columns);

    QueryBuilder groupBy(@NonNull Projection... projections);

    QueryBuilder orderByAscending(@NonNull String... columns);

    QueryBuilder orderByAscending(Projection... projections);

    QueryBuilder orderByAscendingIgnoreCase(@NonNull String... columns);

    QueryBuilder orderByAscendingIgnoreCase(Projection... projections);

    QueryBuilder orderByDescending(@NonNull String... columns);

    QueryBuilder orderByDescending(Projection... projections);

    QueryBuilder orderByDescendingIgnoreCase(@NonNull String... columns);

    QueryBuilder orderByDescendingIgnoreCase(Projection... projections);

    QueryBuilder union(@NonNull QueryBuilder query);

    QueryBuilder unionAll(@NonNull QueryBuilder query);

    QueryBuilder select(@NonNull String... columns);

    QueryBuilder select(@NonNull Projection... projections);

    QueryBuilder whereAnd(@NonNull Criteria criteria);

    QueryBuilder whereOr(@NonNull Criteria criteria);

    QueryBuilder withDateFormat(@NonNull String format);

    QueryBuilder withDateTimeFormat(@NonNull String format);

    QueryBuilder offset(int skip);

    QueryBuilder offsetNone();

    QueryBuilder limit(int limit);

    QueryBuilder limitAll();

}
