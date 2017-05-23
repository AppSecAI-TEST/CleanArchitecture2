package com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.criteria;

import com.cleanarchitecture.shishkin.base.database.querybuilder.QueryBuilderUtils;
import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.SqliteQueryBuilder;

import java.util.List;

public class ExistsCriteria extends Criteria {
	private SqliteQueryBuilder subQuery;
	
	public ExistsCriteria(SqliteQueryBuilder subQuery) {
		this.subQuery = subQuery;
	}

	@Override
	public String build() {
		String ret = "EXISTS(";
		
		if(subQuery != null)
			ret = ret + subQuery.build();
		
		ret = ret + ")";
		return ret;
	}

	@Override
	public List<Object> buildParameters() {
		if(subQuery != null)
			return subQuery.buildParameters();
		else
			return QueryBuilderUtils.EMPTY_LIST;
	}
}
