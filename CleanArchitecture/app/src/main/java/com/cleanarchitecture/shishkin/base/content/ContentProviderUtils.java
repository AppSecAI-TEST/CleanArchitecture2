package com.cleanarchitecture.shishkin.base.content;

import android.net.Uri;
import android.support.annotation.NonNull;

public final class ContentProviderUtils {

    public static final String SCHEME_CONTENT = "content";

    @NonNull
    public static Uri createContentUri(@NonNull final String authority, @NonNull final String table) {
        return Uri.parse(SCHEME_CONTENT + "://" + authority + "/" + table);
    }

    private ContentProviderUtils() {
    }

}