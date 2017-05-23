package com.cleanarchitecture.shishkin.base.database;

import android.support.annotation.NonNull;

public class Default {

    public static final Default NULL = new Default("NULL");
    public static final Default CURRENT_TIMESTAMP = new Default("CURRENT_TIMESTAMP");

    @SuppressWarnings("all")
    @NonNull
    public static Default from(@NonNull final String value) {
        if (value == null) {
            throw new IllegalArgumentException("Use Default.NULL instead of Default#from");
        }
        return new Default("\"" + value + "\"");
    }

    @NonNull
    public static Default from(final int value) {
        return new Default(Integer.toString(value));
    }

    @NonNull
    public static Default from(final long value) {
        return new Default(Long.toString(value));
    }

    @NonNull
    public static Default from(final float value) {
        return new Default(Float.toString(value));
    }

    @NonNull
    public static Default from(final double value) {
        return new Default(Double.toString(value));
    }

    @NonNull
    private final String mValue;

    private Default(@NonNull final String value) {
        mValue = value;
    }

    @NonNull
    /* package */ String toSql() {
        return "DEFAULT " + mValue;
    }

}
