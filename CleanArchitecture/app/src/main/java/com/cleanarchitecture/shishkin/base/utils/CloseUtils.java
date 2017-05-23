package com.cleanarchitecture.shishkin.base.utils;

import com.github.snowdream.android.util.Log;

import java.io.Closeable;
import java.io.IOException;

public class CloseUtils {
    private static final String LOG_TAG = "CloseUtils:";

    private CloseUtils() {
    }

    public static void closeIO(Closeable... closeables) {
        if (closeables == null) return;
        try {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

}
