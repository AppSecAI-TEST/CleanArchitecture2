package com.cleanarchitecture.shishkin.base.repository.requests;

public interface IRequest extends Runnable{
    int getRank();

    IRequest setRank(int rank);

    int getCacheType();

    IRequest setCacheType(int cacheType);
}
