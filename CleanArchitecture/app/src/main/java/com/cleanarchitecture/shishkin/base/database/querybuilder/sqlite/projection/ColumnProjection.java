package com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.projection;

import com.cleanarchitecture.shishkin.base.database.querybuilder.QueryBuilderUtils;

import java.util.List;

public class ColumnProjection extends Projection {
    private String table;
    private String column;

    public ColumnProjection(String table, String column) {
        this.table = table;
        this.column = column;
    }

    @Override
    public String build() {
        String ret = "";

        if (!QueryBuilderUtils.isNullOrWhiteSpace(table))
            ret = ret + table + ".";

        if (!QueryBuilderUtils.isNullOrWhiteSpace(column))
            ret = ret + column;

        return ret;
    }

    @Override
    public List<Object> buildParameters() {
        return QueryBuilderUtils.EMPTY_LIST;
    }
}
