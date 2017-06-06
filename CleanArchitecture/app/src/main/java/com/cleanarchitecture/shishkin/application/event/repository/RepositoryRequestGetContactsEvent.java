package com.cleanarchitecture.shishkin.application.event.repository;

import com.cleanarchitecture.shishkin.base.event.AbstractEvent;
import com.cleanarchitecture.shishkin.base.repository.Repository;

public class RepositoryRequestGetContactsEvent extends AbstractEvent {

    private int mCacheType = Repository.USE_ONLY_CACHE;

    public int getCacheType() {
        return mCacheType;
    }

    public RepositoryRequestGetContactsEvent setCacheType(final int cacheType) {
        this.mCacheType = cacheType;
        return this;
    }


}
