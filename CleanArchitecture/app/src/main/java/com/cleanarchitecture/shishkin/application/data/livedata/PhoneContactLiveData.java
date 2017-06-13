package com.cleanarchitecture.shishkin.application.data.livedata;

import com.cleanarchitecture.shishkin.application.app.Constant;
import com.cleanarchitecture.shishkin.application.data.dao.PhoneContactDAO;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetContactsEvent;
import com.cleanarchitecture.shishkin.api.controller.Admin;
import com.cleanarchitecture.shishkin.api.data.AbstractContentProviderLiveData;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarHideProgressBarEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarShowProgressBarEvent;
import com.cleanarchitecture.shishkin.api.repository.Repository;
import com.cleanarchitecture.shishkin.api.storage.DiskCache;
import com.cleanarchitecture.shishkin.api.storage.IStorage;
import com.cleanarchitecture.shishkin.api.storage.MemoryCache;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PhoneContactLiveData extends AbstractContentProviderLiveData<List<PhoneContactItem>> {
    public static final String NAME = "PhoneContactLiveData";

    public PhoneContactLiveData() {
        super(PhoneContactDAO.CONTENT_URI);

        setDebounce(TimeUnit.SECONDS.toMillis(2));
    }

    @Override
    public void getData() {
        AdminUtils.postEvent(new ToolbarShowProgressBarEvent());
        AdminUtils.postEvent(new RepositoryRequestGetContactsEvent()
                .setCacheType(Repository.USE_ONLY_CACHE)
                .setId(Constant.REPOSITORY_GET_CONTACTS));
    }

    @Override
    public void onChanged() {
        final IStorage memoryCache = Admin.getInstance().get(MemoryCache.NAME);
        if (memoryCache != null) {
            memoryCache.clear(String.valueOf(Constant.REPOSITORY_GET_CONTACTS));

        }

        final IStorage diskCache = Admin.getInstance().get(DiskCache.NAME);
        if (diskCache != null) {
            diskCache.clear(String.valueOf(Constant.REPOSITORY_GET_CONTACTS));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onResponseGetContactsEvent(RepositoryResponseGetContactsEvent event) {
        AdminUtils.postEvent(new ToolbarHideProgressBarEvent());
        if (!event.hasError()) {
            setValue(event.getResponse());
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
