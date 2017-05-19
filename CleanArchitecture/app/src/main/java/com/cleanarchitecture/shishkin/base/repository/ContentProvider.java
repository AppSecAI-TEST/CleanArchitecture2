package com.cleanarchitecture.shishkin.base.repository;

public class ContentProvider {
    public static final String NAME = "ContentProvider";

    private static volatile ContentProvider sInstance;

    public static synchronized void instantiate() {
        if (sInstance == null) {
            synchronized (ContentProvider.class) {
                if (sInstance == null) {
                    sInstance = new ContentProvider();
                }
            }
        }
    }

    public static ContentProvider getInstance() {
        instantiate();
        return sInstance;
    }

    private ContentProvider() {
    }

}
