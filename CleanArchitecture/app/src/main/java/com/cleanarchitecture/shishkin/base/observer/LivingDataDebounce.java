package com.cleanarchitecture.shishkin.base.observer;

import com.cleanarchitecture.shishkin.base.data.AbstractContentProviderLivingData;

public class LivingDataDebounce<T extends AbstractContentProviderLivingData> extends Debounce {

    private T mLivingData = null;

    public LivingDataDebounce(final T data, final long delay) {
        super(delay);
        mLivingData = data;
    }

    @Override
    public void run() {
        if (mLivingData != null) {
            mLivingData.getData();
        }
    }

}
