package com.cleanarchitecture.shishkin.application.data.livedata;

import com.cleanarchitecture.shishkin.api.model.AbstractContentProviderLiveData;
import com.cleanarchitecture.shishkin.api.model.IDatastore;
import com.cleanarchitecture.shishkin.application.data.dao.PhoneContactDAO;
import com.cleanarchitecture.shishkin.application.data.datastore.PhoneContactDatastore;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;

import java.util.List;

@SuppressWarnings("unused")
public class PhoneContactLiveData extends AbstractContentProviderLiveData<List<PhoneContactItem>> {

    public PhoneContactLiveData() {
        super(PhoneContactDAO.CONTENT_URI);
    }

    @Override
    public IDatastore getDatastore() {
        return new PhoneContactDatastore(this);
    }
}
