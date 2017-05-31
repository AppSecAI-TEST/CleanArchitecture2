package com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.from;

import com.cleanarchitecture.shishkin.base.database.querybuilder.QueryBuilderUtils;
import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.SqliteQueryBuilder;

import java.util.List;

public class SubQueryFrom extends AliasableFrom<SubQueryFrom> {
    private SqliteQueryBuilder subQuery;

    public SubQueryFrom(SqliteQueryBuilder subQuery) {
        this.subQuery = subQuery;
    }

    @Override
    public String build() {
        String ret = (subQuery != null ? "(" + subQuery.build() + ")" : "");

        if (!QueryBuilderUtils.isNullOrWhiteSpace(alias))
            ret = ret + " AS " + alias;

        return ret;
    }

    @Override
    public List<Object> buildParameters() {
        if (subQuery != null)
            return subQuery.buildParameters();
        else
            return QueryBuilderUtils.EMPTY_LIST;
    }
}
