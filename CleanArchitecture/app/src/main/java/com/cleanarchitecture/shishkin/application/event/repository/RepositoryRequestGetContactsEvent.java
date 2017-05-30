package com.cleanarchitecture.shishkin.application.event.repository;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.base.event.AbstractEvent;
import com.cleanarchitecture.shishkin.base.repository.Repository;

public class RepositoryRequestGetContactsEvent extends AbstractEvent {

    private int mCacheType = Repository.USE_ONLY_CACHE;

    public RepositoryRequestGetContactsEvent(final int cacheType) {
        setId(R.id.repository_get_contacts);

        mCacheType = cacheType;
    }

    public int getCacheType() {
        return mCacheType;
    }
}
