package com.cleanarchitecture.shishkin.application.database.viewmodel;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.cleanarchitecture.shishkin.application.database.CleanArchitectureDb;
import com.cleanarchitecture.shishkin.application.database.item.Contact;
import com.cleanarchitecture.shishkin.base.controller.Controllers;

import java.util.List;

public class ContactViewModel extends AndroidViewModel {

    private LiveData<List<Contact>> mList;

    public ContactViewModel(Application application) {
        super(application);

        subscribe();
    }

    private void subscribe() {
        final CleanArchitectureDb db = Controllers.getInstance().getRepository().getDbProvider().getDb(CleanArchitectureDb.class, CleanArchitectureDb.NAME);
        mList = db.contactDao().get();
    }

    public LiveData<List<Contact>> getLiveData() {
        return mList;
    }


}
