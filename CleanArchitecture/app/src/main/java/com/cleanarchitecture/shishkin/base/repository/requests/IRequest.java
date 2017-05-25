package com.cleanarchitecture.shishkin.base.repository.requests;

public interface IRequest {
    int getRank();

    IRequest setRank(int rank);

    void run();

    int getCacheType();
}
