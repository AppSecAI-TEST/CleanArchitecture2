package com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.criteria;

import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.SqliteQueryBuilder;

public class NotExistsCriteria extends ExistsCriteria {
	public NotExistsCriteria(SqliteQueryBuilder subQuery) {
		super(subQuery);
	}
	
	@Override
	public String build() {
		return "NOT " + super.build();
	}
}
