package com.cleanarchitecture.shishkin.application.data.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.data.livingdata.PhoneContactLivingData;
import com.cleanarchitecture.shishkin.base.data.AbstractViewModel;

import java.util.List;

public class PhoneContactViewModel extends AbstractViewModel<List<PhoneContactItem>> {
    public static final String NAME = "PhoneContactViewModel";

    private PhoneContactLivingData mData;

    public PhoneContactViewModel(Application application) {
        super(application);
    }

    @Override
    public void subscribe() {
        mData = new PhoneContactLivingData();
    }

    @Override
    public LiveData<List<PhoneContactItem>> getLiveData() {
        return mData;
    }

    @Override
    public String getName() {
        return NAME;
    }

}