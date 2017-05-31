package com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.projection;

import com.cleanarchitecture.shishkin.base.database.querybuilder.QueryBuilderUtils;

import java.util.ArrayList;
import java.util.List;

public class ConstantProjection extends Projection {
    private Object constant;

    public ConstantProjection(Object constant) {
        this.constant = constant;
    }

    @Override
    public String build() {
        if (constant != null)
            return "?";
        else
            return "NULL";
    }

    @Override
    public List<Object> buildParameters() {
        if (constant != null) {
            List<Object> ret = new ArrayList<>();
            ret.add(constant);

            return ret;
        } else {
            return QueryBuilderUtils.EMPTY_LIST;
        }
    }
}
