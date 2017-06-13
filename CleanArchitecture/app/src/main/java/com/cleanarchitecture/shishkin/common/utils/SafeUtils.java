package com.cleanarchitecture.shishkin.common.utils;

import com.github.snowdream.android.util.Log;

/**
 * {@code SafeUtils} contains static methods to perform safe operation like object casting.
 */
public class SafeUtils {

    private static final String LOG_TAG = "SafeUtils:";

    public static <C> C cast(final Object o) {
        if (o == null) {
            return null;
        }

        try {
            return (C) o;
        } catch (final ClassCastException cce) {
            Log.e(LOG_TAG, cce.getMessage());
            return null;
        } catch (final Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            return null;
        }
    }

    private SafeUtils() {
    }

}
