package com.cleanarchitecture.shishkin.base.utils;

import com.cleanarchitecture.shishkin.base.controller.ErrorController;
import com.github.snowdream.android.util.Log;

import java.io.Closeable;
import java.io.IOException;

public class CloseUtils {
    private static final String LOG_TAG = "CloseUtils:";

    private CloseUtils() {
    }

    public static void close(Closeable... closeables) {
        if (closeables == null) return;
        try {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        } catch (IOException e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        }
    }

}
