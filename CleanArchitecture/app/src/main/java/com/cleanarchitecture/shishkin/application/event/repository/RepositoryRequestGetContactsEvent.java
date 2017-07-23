package com.cleanarchitecture.shishkin.application.event.repository;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;
import com.cleanarchitecture.shishkin.api.storage.CacheUtils;
import com.cleanarchitecture.shishkin.application.Constant;

public class RepositoryRequestGetContactsEvent extends AbstractEvent {

    private int mCacheType = CacheUtils.USE_ONLY_CACHE;
    private long mExpired = 0;

    public RepositoryRequestGetContactsEvent() {
        super(Constant.REPOSITORY_REQUEST_GET_CONTACTS_EVENT);
    }

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
