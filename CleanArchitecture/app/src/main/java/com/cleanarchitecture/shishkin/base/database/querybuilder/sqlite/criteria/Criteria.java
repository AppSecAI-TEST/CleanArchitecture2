package com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.criteria;

import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.SqliteQueryBuilder;
import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.projection.Projection;

import java.util.List;

public abstract class Criteria {
    // Null
    public static Criteria isNull(String column) {
        return new BasicCriteria(Projection.column(column), BasicCriteria.Operators.IS_NULL, null);
    }

    public static Criteria notIsNull(String column) {
        return new BasicCriteria(Projection.column(column), BasicCriteria.Operators.IS_NOT_NULL, null);
    }

    public static Criteria isNull(Projection projection) {
        return new BasicCriteria(projection, BasicCriteria.Operators.IS_NULL, null);
    }

    public static Criteria notIsNull(Projection projection) {
        return new BasicCriteria(projection, BasicCriteria.Operators.IS_NOT_NULL, null);
    }


    // Basic criterias
    public static Criteria equals(String column, Object value) {
        return new BasicCriteria(Projection.column(column), BasicCriteria.Operators.EQUALS, value);
    }

    public static Criteria notEquals(String column, Object value) {
        return new BasicCriteria(Projection.column(column), BasicCriteria.Operators.NOT_EQUALS, value);
    }

    public static Criteria greaterThan(String column, Object value) {
        return new BasicCriteria(Projection.column(column), BasicCriteria.Operators.GREATER, value);
    }

    public static Criteria lesserThan(String column, Object value) {
        return new BasicCriteria(Projection.column(column), BasicCriteria.Operators.LESSER, value);
    }

    public static Criteria greaterThanOrEqual(String column, Object value) {
        return new BasicCriteria(Projection.column(column), BasicCriteria.Operators.GREATER_OR_EQUALS, value);
    }

    public static Criteria lesserThanOrEqual(String column, Object value) {
        return new BasicCriteria(Projection.column(column), BasicCriteria.Operators.LESSER_OR_EQUALS, value);
    }

    public static Criteria equals(Projection column, Object value) {
        return new BasicCriteria(column, BasicCriteria.Operators.EQUALS, value);
    }

    public static Criteria notEquals(Projection column, Object value) {
        return new BasicCriteria(column, BasicCriteria.Operators.NOT_EQUALS, value);
    }

    public static Criteria greaterThan(Projection column, Object value) {
        return new BasicCriteria(column, BasicCriteria.Operators.GREATER, value);
    }

    public static Criteria lesserThan(Projection column, Object value) {
        return new BasicCriteria(column, BasicCriteria.Operators.LESSER, value);
    }

    public static Criteria greaterThanOrEqual(Projection column, Object value) {
        return new BasicCriteria(column, BasicCriteria.Operators.GREATER_OR_EQUALS, value);
    }

    public static Criteria lesserThanOrEqual(Projection column, Object value) {
        return new BasicCriteria(column, BasicCriteria.Operators.LESSER_OR_EQUALS, value);
    }


    // String-only criterias
    public static Criteria startsWith(String column, String value) {
        return new BasicCriteria(Projection.column(column), BasicCriteria.Operators.LIKE, value + "%");
    }

    public static Criteria notStartsWith(String column, String value) {
        return new BasicCriteria(Projection.column(column), BasicCriteria.Operators.NOT_LIKE, value + "%");
    }

    public static Criteria endsWith(String column, String value) {
        return new BasicCriteria(Projection.column(column), BasicCriteria.Operators.LIKE, "%" + value);
    }

    public static Criteria notEndsWith(String column, String value) {
        return new BasicCriteria(Projection.column(column), BasicCriteria.Operators.NOT_LIKE, "%" + value);
    }

    public static Criteria contains(String column, String value) {
        return new BasicCriteria(Projection.column(column), BasicCriteria.Operators.LIKE, "%" + value + "%");
    }

    public static Criteria notContains(String column, String value) {
        return new BasicCriteria(Projection.column(column), BasicCriteria.Operators.NOT_LIKE, "%" + value + "%");
    }

    public static Criteria startsWith(Projection column, String value) {
        return new BasicCriteria(column, BasicCriteria.Operators.LIKE, value + "%");
    }

    public static Criteria notStartsWith(Projection column, String value) {
        return new BasicCriteria(column, BasicCriteria.Operators.NOT_LIKE, value + "%");
    }

    public static Criteria endsWith(Projection column, String value) {
        return new BasicCriteria(column, BasicCriteria.Operators.LIKE, "%" + value);
    }

    public static Criteria notEndsWith(Projection column, String value) {
        return new BasicCriteria(column, BasicCriteria.Operators.NOT_LIKE, "%" + value);
    }

    public static Criteria contains(Projection column, String value) {
        return new BasicCriteria(column, BasicCriteria.Operators.LIKE, "%" + value + "%");
    }

    public static Criteria notContains(Projection column, String value) {
        return new BasicCriteria(column, BasicCriteria.Operators.NOT_LIKE, "%" + value + "%");
    }


    // Between
    public static Criteria between(String column, Object valueMin, Object valueMax) {
        return new BetweenCriteria(Projection.column(column), valueMin, valueMax);
    }

    public static Criteria valueBetween(Object value, String columnMin, String columnMax) {
        return new ValueBetweenCriteria(value, Projection.column(columnMin), Projection.column(columnMax));
    }

    public static Criteria between(Projection column, Object valueMin, Object valueMax) {
        return new BetweenCriteria(column, valueMin, valueMax);
    }

    public static Criteria valueBetween(Object value, Projection columnMin, Projection columnMax) {
        return new ValueBetweenCriteria(value, columnMin, columnMax);
    }


    // Exists
    public static Criteria exists(SqliteQueryBuilder subQuery) {
        return new ExistsCriteria(subQuery);
    }

    public static Criteria notExists(SqliteQueryBuilder subQuery) {
        return new NotExistsCriteria(subQuery);
    }


    // In
    public static Criteria in(String column, Object[] values) {
        return new InCriteria(Projection.column(column), values);
    }

    public static Criteria notIn(String column, Object[] values) {
        return new NotInCriteria(Projection.column(column), values);
    }

    public static Criteria in(String column, List<Object> values) {
        return new InCriteria(Projection.column(column), values);
    }

    public static Criteria notIn(String column, List<Object> values) {
        return new NotInCriteria(Projection.column(column), values);
    }

    public static Criteria in(Projection column, Object[] values) {
        return new InCriteria(column, values);
    }

    public static Criteria notIn(Projection column, Object[] values) {
        return new NotInCriteria(column, values);
    }

    public static Criteria in(Projection column, List<Object> values) {
        return new InCriteria(column, values);
    }

    public static Criteria notIn(Projection column, List<Object> values) {
        return new NotInCriteria(column, values);
    }

    public abstract String build();

    public abstract List<Object> buildParameters();

    public AndCriteria and(Criteria criteria) {
        return new AndCriteria(this, criteria);
    }

    public OrCriteria or(Criteria criteria) {
        return new OrCriteria(this, criteria);
    }
}
