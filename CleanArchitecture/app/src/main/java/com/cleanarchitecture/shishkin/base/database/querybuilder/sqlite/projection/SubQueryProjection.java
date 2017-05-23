package com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.projection;

import com.cleanarchitecture.shishkin.base.database.querybuilder.QueryBuilderUtils;
import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.SqliteQueryBuilder;

import java.util.List;

public class SubQueryProjection extends Projection {
	private SqliteQueryBuilder subQuery;
	
	public SubQueryProjection(SqliteQueryBuilder subQuery) {
		this.subQuery = subQuery;
	}

	@Override
	public String build() {
		if(subQuery != null)
			return "(" + subQuery.build() + ")";
		else
			return "";
	}

	@Override
	public List<Object> buildParameters() {
		if(subQuery != null)
			return subQuery.buildParameters();
		else
			return QueryBuilderUtils.EMPTY_LIST;
	}
}
