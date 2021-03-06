package com.cleanarchitecture.shishkin.application.data.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.cleanarchitecture.shishkin.api.model.AbstractViewModel;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.data.livedata.PhoneContactLiveData;

import java.util.List;

public class PhoneContactViewModel extends AbstractViewModel<List<PhoneContactItem>> {
    public static final String NAME = PhoneContactViewModel.class.getName();

    private PhoneContactLiveData mData;

    public PhoneContactViewModel(Application application) {
        super(application);
    }

    @Override
    public void setLiveData() {
        mData = new PhoneContactLiveData();
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
