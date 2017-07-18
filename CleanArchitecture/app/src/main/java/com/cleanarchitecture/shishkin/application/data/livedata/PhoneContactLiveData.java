package com.cleanarchitecture.shishkin.application.data.livedata;

import com.cleanarchitecture.shishkin.api.controller.Admin;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.api.event.ui.HideHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.api.model.AbstractContentProviderLiveData;
import com.cleanarchitecture.shishkin.api.storage.CacheUtils;
import com.cleanarchitecture.shishkin.api.storage.IExpiredParcelableStorage;
import com.cleanarchitecture.shishkin.api.storage.IParcelableStorage;
import com.cleanarchitecture.shishkin.api.storage.ParcelableDiskCache;
import com.cleanarchitecture.shishkin.api.storage.ParcelableMemoryCache;
import com.cleanarchitecture.shishkin.application.Constant;
import com.cleanarchitecture.shishkin.application.data.dao.PhoneContactDAO;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetContactsEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class PhoneContactLiveData extends AbstractContentProviderLiveData<List<PhoneContactItem>> {
    public static final String NAME = PhoneContactLiveData.class.getName();

    public PhoneContactLiveData() {
        super(PhoneContactDAO.CONTENT_URI);

        setDebounce(TimeUnit.SECONDS.toMillis(2));
    }

    @Override
    public void getData() {
        AdminUtils.postEvent(new ShowHorizontalProgressBarEvent());
        AdminUtils.postEvent(new RepositoryRequestGetContactsEvent()
                .setExpired(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1))
                .setCacheType(CacheUtils.USE_CACHE)
                .setId(Constant.REPOSITORY_GET_CONTACTS));
    }

    @Override
    public void onChanged() {
        final IParcelableStorage memoryCache = Admin.getInstance().get(ParcelableMemoryCache.NAME);
        if (memoryCache != null) {
            memoryCache.clear(String.valueOf(Constant.REPOSITORY_GET_CONTACTS));
        }

        final IExpiredParcelableStorage diskCache = Admin.getInstance().get(ParcelableDiskCache.NAME);
        if (diskCache != null) {
            diskCache.clear(String.valueOf(Constant.REPOSITORY_GET_CONTACTS));
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onResponseGetContactsEvent(RepositoryResponseGetContactsEvent event) {
        AdminUtils.postEvent(new HideHorizontalProgressBarEvent());
        if (!event.hasError()) {
            setValue(event.getResponse());
        } else {
            ErrorController.getInstance().onError(event.getError());
        }
    }

}
