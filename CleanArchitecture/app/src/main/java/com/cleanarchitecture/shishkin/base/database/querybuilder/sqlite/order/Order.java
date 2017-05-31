package com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.order;

import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.projection.AliasedProjection;
import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.projection.Projection;

import java.util.List;

public abstract class Order {
    public static Order orderByAscending(String column) {
        return new OrderAscending(Projection.column(column));
    }

    public static Order orderByDescending(String column) {
        return new OrderDescending(Projection.column(column));
    }

    public static Order orderByAscending(Projection projection) {
        return new OrderAscending(projection);
    }

    public static Order orderByDescending(Projection projection) {
        return new OrderDescending(projection);
    }

    public static Order orderByAscendingIgnoreCase(String column) {
        return new OrderAscendingIgnoreCase(Projection.column(column));
    }

    public static Order orderByDescendingIgnoreCase(String column) {
        return new OrderDescendingIgnoreCase(Projection.column(column));
    }

    public static Order orderByAscendingIgnoreCase(Projection projection) {
        return new OrderAscendingIgnoreCase(projection);
    }

    public static Order orderByDescendingIgnoreCase(Projection projection) {
        return new OrderDescendingIgnoreCase(projection);
    }


    protected Projection projection;

    public Order(Projection projection) {
        this.projection = projection;

        if (this.projection instanceof AliasedProjection)
            this.projection = ((AliasedProjection) this.projection).removeAlias();
    }

    public abstract String build();

    public abstract List<Object> buildParameters();
}
