package com.cleanarchitecture.shishkin.application.event.repository;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;
import com.cleanarchitecture.shishkin.api.storage.CacheUtils;

public class RepositoryRequestGetContactsEvent extends AbstractEvent {

    private int mCacheType = CacheUtils.USE_ONLY_CACHE;
    private long mExpired = 0;

    public int getCacheType() {
        return mCacheType;
    }

    public long getExpired() {
        return mExpired;
    }

    public RepositoryRequestGetContactsEvent setCacheType(final int cacheType) {
        this.mCacheType = cacheType;
        return this;
    }

    public RepositoryRequestGetContactsEvent setExpired(long expired) {
        mExpired = expired;
        return this;
    }
}
