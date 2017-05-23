package com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.from;

import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.SqliteQueryBuilder;
import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.criteria.Criteria;
import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.projection.Projection;

import java.util.List;

public abstract class From {
	public static class PartialJoin {
		private String joinType;
		private From left;
		private From right;

		protected PartialJoin(From left, From right, String joinType) {
			this.joinType = joinType;
			this.left = left;
			this.right = right;
		}
		
		public JoinFrom on(String leftColumn, String rightColumn) {
			return on(Criteria.equals(Projection.column(leftColumn), Projection.column(rightColumn)));
		}
		
		public JoinFrom on(Criteria criteria) {
			return new JoinFrom(left, right, joinType, criteria);
		}
	}
	
	public static TableFrom table(String table) {
		return new TableFrom(table);
	}
	
	public static SubQueryFrom subQuery(SqliteQueryBuilder subQuery) {
		return new SubQueryFrom(subQuery);
	}
	
	public PartialJoin innerJoin(String table) {
		return innerJoin(From.table(table));
	}
	
	public PartialJoin innerJoin(SqliteQueryBuilder subQuery) {
		return innerJoin(From.subQuery(subQuery));
	}
	
	public PartialJoin innerJoin(From table) {
		return new PartialJoin(this, table, "INNER JOIN");
	}
	
	public PartialJoin leftJoin(String table) {
		return leftJoin(From.table(table));
	}
	
	public PartialJoin leftJoin(SqliteQueryBuilder subQuery) {
		return leftJoin(From.subQuery(subQuery));
	}
	
	public PartialJoin leftJoin(From table) {
		return new PartialJoin(this, table, "LEFT JOIN");
	}
	
	public abstract String build();
	
	public abstract List<Object> buildParameters();
}
