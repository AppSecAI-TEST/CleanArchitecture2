package com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.from;

import com.cleanarchitecture.shishkin.base.database.querybuilder.QueryBuilderUtils;

import java.util.List;

public class TableFrom extends AliasableFrom<TableFrom> {
	private String table;
	
	public TableFrom(String table) {
		this.table = table;
	}

	@Override
	public String build() {
		String ret = (!QueryBuilderUtils.isNullOrWhiteSpace(table) ? table : "");

		if(!QueryBuilderUtils.isNullOrWhiteSpace(alias))
			ret = ret + " AS " + alias;
		
		return ret;
	}

	@Override
	public List<Object> buildParameters() {
		return QueryBuilderUtils.EMPTY_LIST;
	}
}
