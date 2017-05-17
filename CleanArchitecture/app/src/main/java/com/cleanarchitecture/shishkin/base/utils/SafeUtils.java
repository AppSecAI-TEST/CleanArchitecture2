package com.cleanarchitecture.shishkin.base.utils;

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
            Log.e(LOG_TAG, "Failed to cast " + o + ".", cce);
            return null;
        } catch (final Exception e) {
            Log.e(LOG_TAG, "Failed to cast " + o + ".", e);
            return null;
        }
    }
}
