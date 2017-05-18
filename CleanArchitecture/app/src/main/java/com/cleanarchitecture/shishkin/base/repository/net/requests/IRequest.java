package com.cleanarchitecture.shishkin.base.repository.net.requests;

public interface IRequest {
    int getRank();

    void setRank(int rank);

    void run();
}
