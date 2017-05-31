package com.cleanarchitecture.shishkin.base.database.querybuilder;

import com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite.projection.Projection;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class QueryBuilderUtils {
    public static final List<Object> EMPTY_LIST = new ArrayList<Object>();
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public static String toString(Object value) {
        if (value == null)
            return null;

        if (value instanceof String)
            return (String) value;
        else if (value instanceof Float)
            return new BigDecimal((Float) value).stripTrailingZeros().toPlainString();
        else if (value instanceof Double)
            return new BigDecimal((Double) value).stripTrailingZeros().toPlainString();
        else
            return String.valueOf(value);
    }

    public static String dateToString(LocalDate date, DateTimeFormatter format) {
        if (date == null)
            return null;

        if (format == null)
            format = DATE_FORMATTER;

        try {
            return date.toString(format);
        } catch (Exception e) {
            return null;
        }
    }

    public static String dateToString(LocalDateTime date, DateTimeFormatter format) {
        if (date == null)
            return null;

        if (format == null)
            format = DATE_TIME_FORMATTER;

        try {
            return date.toString(format);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isNullOrEmpty(final String string) {
        return (string == null || string.length() <= 0);
    }

    public static boolean isNullOrWhiteSpace(final String string) {
        return (string == null || string.trim().length() <= 0);
    }

    public static Projection[] buildColumnProjections(String... columns) {
        Projection[] projections = new Projection[columns.length];

        for (int i = 0; i < columns.length; i++) {
            projections[i] = Projection.column(columns[i]);
        }
        return projections;
    }
}
