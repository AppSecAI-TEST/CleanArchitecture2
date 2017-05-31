package com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.projection;

import com.cleanarchitecture.shishkin.base.database.querybuilder.QueryBuilderUtils;

import java.util.List;

public class CastIntProjection extends Projection {
    private Projection projection;

    public CastIntProjection(Projection projection) {
        this.projection = projection;
    }

    @Override
    public String build() {
        String ret = (projection != null ? projection.build() : "");
        return "CAST(" + ret + " AS INTEGER)";
    }

    @Override
    public List<Object> buildParameters() {
        if (projection != null)
            return projection.buildParameters();
        else
            return QueryBuilderUtils.EMPTY_LIST;
    }
}
