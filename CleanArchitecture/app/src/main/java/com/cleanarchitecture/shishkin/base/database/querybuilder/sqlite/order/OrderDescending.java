package com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.order;

import com.cleanarchitecture.shishkin.base.database.querybuilder.QueryBuilderUtils;
import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.projection.Projection;

import java.util.List;

public class OrderDescending extends Order {

    public OrderDescending(Projection projection) {
        super(projection);
    }

    @Override
    public String build() {
        String ret = " DESC";

        if (projection != null)
            ret = projection.build() + ret;

        return ret;
    }

    @Override
    public List<Object> buildParameters() {
        if (projection != null)
            return projection.buildParameters();
        else
            return QueryBuilderUtils.EMPTY_LIST;
    }
}
