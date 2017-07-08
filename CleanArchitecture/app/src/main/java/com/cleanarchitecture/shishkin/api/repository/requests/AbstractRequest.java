package com.cleanarchitecture.shishkin.api.repository.requests;

import com.cleanarchitecture.shishkin.api.storage.CacheUtils;

public abstract class AbstractRequest implements Runnable, IRequest {
    public static final int MAX_RANK = 10;
    public static final int HIGH_RANK = 8;
    public static final int MIDDLE_RANK = 5;
    public static final int LOW_RANK = 2;
    public static final int MIN_RANK = 0;

    private int mRank = MIN_RANK;
    private int mCacheType = CacheUtils.USE_ONLY_MEMORY_CACHE;

    public AbstractRequest(final int rank) {
        mRank = rank;
    }

    @Override
    public int getRank() {
        return mRank;
    }

    @Override
    public IRequest setRank(int rank) {
        this.mRank = rank;
        return this;
    }

    @Override
    public int getCacheType() {
        return mCacheType;
    }

    @Override
    public IRequest setCacheType(final int cacheType) {
        mCacheType = cacheType;
        return this;
    }
}
