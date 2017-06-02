package com.cleanarchitecture.shishkin.application.database.viewmodel;


import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.cleanarchitecture.shishkin.application.database.CleanArchitectureDb;
import com.cleanarchitecture.shishkin.application.database.item.Contact;
import com.cleanarchitecture.shishkin.base.controller.Controllers;
import com.cleanarchitecture.shishkin.base.room.AbstractViewModel;

import java.util.List;

public class ContactViewModel extends AbstractViewModel<List<Contact>> {

    public static final String NAME = "ContactViewModel";
    private LiveData<List<Contact>> mList;

    public ContactViewModel(Application application) {
        super(application);
    }

    @Override
    public void subscribe() {
        final CleanArchitectureDb db = Controllers.getInstance().getDb(CleanArchitectureDb.class, CleanArchitectureDb.NAME);
        if (db != null) {
            mList = db.contactDao().get();
        }
    }

    @Override
    public LiveData<List<Contact>> getLiveData() {
        return mList;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
