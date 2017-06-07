package com.cleanarchitecture.shishkin.application.data.livedata;

import android.content.Context;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.application.app.Constant;
import com.cleanarchitecture.shishkin.application.data.dao.PhoneContactDAO;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetContactsEvent;
import com.cleanarchitecture.shishkin.base.data.AbstractContentProviderLiveData;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarHideProgressBarEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarShowProgressBarEvent;
import com.cleanarchitecture.shishkin.base.event.ui.HideHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.base.event.ui.HideProgressBarEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.base.repository.Repository;
import com.cleanarchitecture.shishkin.base.storage.DiskCache;
import com.cleanarchitecture.shishkin.base.storage.MemoryCache;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;

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
        ApplicationUtils.postEvent(new ToolbarShowProgressBarEvent());
        ApplicationUtils.postEvent(new RepositoryRequestGetContactsEvent()
                .setCacheType(Repository.USE_ONLY_CACHE)
                .setId(Constant.REPOSITORY_GET_CONTACTS));
    }

    @Override
    public void onChanged() {
        MemoryCache.getInstance().clear(String.valueOf(Constant.REPOSITORY_GET_CONTACTS));
        final Context context = ApplicationController.getInstance();
        if (context != null) {
            DiskCache.getInstance(context).clear(String.valueOf(Constant.REPOSITORY_GET_CONTACTS));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onResponseGetContactsEvent(RepositoryResponseGetContactsEvent event) {
        ApplicationUtils.postEvent(new ToolbarHideProgressBarEvent());
        ApplicationUtils.postEvent(new HideProgressBarEvent());
        if (!event.hasError()) {
            setValue(event.getResponse());
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
