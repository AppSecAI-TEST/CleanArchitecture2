package com.cleanarchitecture.shishkin.base.utils;

import com.cleanarchitecture.shishkin.base.controller.ErrorController;
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
            ErrorController.getInstance().onError(LOG_TAG, cce);
            return null;
        } catch (final Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
            return null;
        }
    }

    private SafeUtils() {
    }

}
