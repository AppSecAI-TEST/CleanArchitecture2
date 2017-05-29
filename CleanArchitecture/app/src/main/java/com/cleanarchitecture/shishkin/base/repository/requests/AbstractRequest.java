package com.cleanarchitecture.shishkin.base.repository.requests;

public abstract class AbstractRequest implements Runnable, IRequest {
	public static final int MAX_RANK = 10;
	public static final int HIGH_RANK = 8;
	public static final int MIDDLE_RANK = 5;
	public static final int LOW_RANK = 2;
	public static final int MIN_RANK = 0;

	public AbstractRequest(int rank) {
		mRank = rank;
	}

	private int mRank = MIN_RANK;

	@Override
	public int getRank() {
		return mRank;
	}

	@Override
	public IRequest setRank(int rank) {
		this.mRank = rank;
		return this;
	}

	public abstract int getCacheType();

}
