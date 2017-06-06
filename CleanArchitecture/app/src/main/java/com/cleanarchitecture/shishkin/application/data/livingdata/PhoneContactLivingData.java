package com.cleanarchitecture.shishkin.application.data.livingdata;

import com.cleanarchitecture.shishkin.application.data.dao.PhoneContactDAO;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetContactsEvent;
import com.cleanarchitecture.shishkin.base.data.AbstractContentProviderLivingData;
import com.cleanarchitecture.shishkin.base.event.ui.HideHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.base.repository.Repository;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PhoneContactLivingData extends AbstractContentProviderLivingData<List<PhoneContactItem>> {
    public static final String NAME = "PhoneContactLivingData";

    public PhoneContactLivingData() {
        super(PhoneContactDAO.CONTENT_URI);

        setDebounce(TimeUnit.SECONDS.toMillis(2));
    }

    @Override
    public void getData() {
        ApplicationUtils.postEvent(new ShowHorizontalProgressBarEvent());
        ApplicationUtils.postEvent(new RepositoryRequestGetContactsEvent(Repository.USE_ONLY_CACHE));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onResponseGetContactsEvent(RepositoryResponseGetContactsEvent event) {
        ApplicationUtils.postEvent(new HideHorizontalProgressBarEvent());
        if (!event.hasError()) {
            setValue(event.getResponse());
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
