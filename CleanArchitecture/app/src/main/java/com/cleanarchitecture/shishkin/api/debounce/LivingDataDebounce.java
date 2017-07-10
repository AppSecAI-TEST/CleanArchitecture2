package com.cleanarchitecture.shishkin.api.debounce;

import com.cleanarchitecture.shishkin.api.model.AbstractContentProviderLiveData;

public class LivingDataDebounce<T extends AbstractContentProviderLiveData> extends Debounce {

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
